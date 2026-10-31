'use strict';
// Served from shipment-service on http://localhost:8080 (same-origin requests).
const SHIPMENT_API = '';
const PDF_API = 'http://localhost:8081'; // Cross-origin: see optional PdfCorsConfig.java
const STATUSES = ['CREATED','PICKED_UP','IN_TRANSIT','ARRIVED_AT_FACILITY','IN_DELIVERY','DELIVERED'];
const $ = id => document.getElementById(id);
const state = { page: 0, size: 10, totalPages: 0, shipmentId: null, jobs: [], shipmentSummaries: new Map(), polling: false };
const escapeHtml = value => String(value ?? '—').replace(/[&<>"']/g, x => ({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[x]));
const statusClass = value => value === 'COMPLETED' || value === 'DELIVERED' ? 'ok' : value === 'FAILED' ? 'bad' : ['RUNNING','IN_TRANSIT','IN_DELIVERY'].includes(value) ? 'warn' : '';
const badge = value => `<span class="badge-soft ${statusClass(value)}">${escapeHtml(value)}</span>`;
const uuid = value => `<span class="uuid">${escapeHtml(value)}</span>`;
function alertBox(message, type='danger') { const el=$('alert'); el.className=`alert alert-${type}`; el.textContent=message; window.scrollTo({top:0,behavior:'smooth'}); }
function clearAlert(){ $('alert').classList.add('d-none'); }
function validUuid(value){ return /^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$/i.test(value); }
async function request(path, options={}, base=SHIPMENT_API){
  const response = await fetch(base + path, options);
  const raw = await response.text(); let data = raw;
  if (raw) { try { data = JSON.parse(raw); } catch { /* plain text response */ } }
  if (!response.ok) {
    const detail = typeof data === 'string' ? data : (data.detail || data.message || data.error || JSON.stringify(data));
    throw new Error(`HTTP ${response.status} ${response.statusText}: ${detail}`);
  }
  return data;
}
const jsonOptions = (method, body) => ({method,headers:{'Content-Type':'application/json'},body:JSON.stringify(body)});
async function run(operation, successMessage) { clearAlert(); try { await operation(); if(successMessage) alertBox(successMessage,'success'); } catch(e) { console.error(e); alertBox(e.message.includes('Failed to fetch') ? `${e.message}. Check the service is running and (for PDF) that CORS is configured.` : e.message); } }
function switchTab(tab){
  document.querySelectorAll('[data-panel]').forEach(panel => {panel.hidden=panel.dataset.panel!==tab;});
  document.querySelectorAll('[data-tab]').forEach(button=>button.classList.toggle('active',button.dataset.tab===tab));
  if(tab==='statistics') loadStatistics();
  if(tab==='invoices' && !$('invoiceRows').dataset.loaded) searchInvoices();
}
async function loadShipments(){
  const data = await request(`/shipment/getShipmentListPage?page=${state.page}&size=${state.size}`);
  const records = data.content || [];
  records.forEach(item=>state.shipmentSummaries.set(item.shipmentId,item));
  state.totalPages=data.totalPages || 0;
  $('shipmentRows').innerHTML = records.length ? records.map(item=>`<tr><td><strong>${escapeHtml(item.shipmentName)}</strong></td><td>${escapeHtml(item.customerName)}</td><td>${badge(item.shipmentStatus)}</td><td>${uuid(item.shipmentId)}</td><td><button class="btn btn-sm btn-outline-primary" data-open-shipment="${escapeHtml(item.shipmentId)}">Open</button></td></tr>`).join('') : '<tr><td colspan="5" class="muted">No shipments found.</td></tr>';
  $('pagingInfo').textContent=`Page ${state.totalPages ? state.page+1 : 0} of ${state.totalPages} · ${data.totalElements ?? records.length} total`;
  $('prevPage').disabled=state.page===0;
  $('nextPage').disabled=state.page+1>=state.totalPages;
  $('connection').textContent='online';
}
async function openShipment(id){
  if(!validUuid(id)) throw new Error('Enter a valid shipment UUID.');
  state.shipmentId=id;
  let details;
  try { details=await request(`/shipment/getShipmentById/${encodeURIComponent(id)}`); }
  catch(err) {
    const summary=state.shipmentSummaries.get(id);
    if(!summary) throw err;
    details={...summary,shipmentDescription:null};
    console.warn('Shipment details endpoint failed; showing list DTO fallback:',err);
    alertBox('Details endpoint failed (repository DTO projection mismatch). Showing cached list data; status changes still work.','warning');
  }
  if(!details) throw new Error('Shipment details endpoint returned an empty response.');
  $('selectedShipmentTitle').textContent=details.shipmentName || '';
  $('shipmentDetail').innerHTML=`<dl class="row mb-0"><dt class="col-sm-3">Name</dt><dd class="col-sm-9">${escapeHtml(details.shipmentName)}</dd><dt class="col-sm-3">ID</dt><dd class="col-sm-9">${uuid(details.shipmentId || id)}</dd><dt class="col-sm-3">Status</dt><dd class="col-sm-9">${badge(details.shipmentStatus)}</dd><dt class="col-sm-3">Description</dt><dd class="col-sm-9">${escapeHtml(details.shipmentDescription)}</dd></dl>`;
  $('newStatus').value=details.shipmentStatus || 'CREATED';
  $('pdfShipmentId').value=id;
  // PDF data is the only existing endpoint returning comment DTOs; it may fail for incomplete shipments.
  try {
    const pdfData=await request(`/shipment/getShipmentPdfDataByShipmentId/${encodeURIComponent(id)}`);
    renderComments(pdfData.shipmentComments || []);
  } catch (err) {
    $('commentsList').innerHTML=`<p class="text-warning mb-1">Comments could not be loaded via the PDF-data endpoint.</p><p class="small muted mb-0">${escapeHtml(err.message)}. This endpoint dereferences customer and both locations, which can be null for CREATED shipments.</p>`;
  }
}
function renderComments(comments){
  $('commentsList').innerHTML=comments.length ? comments.map(c=>`<div class="comment-entry"><div class="d-flex justify-content-between gap-2"><strong>${escapeHtml(c.addedBy || 'Anonymous')}</strong><span class="small muted">${escapeHtml(c.commentDate || '')}</span></div><div class="mt-1">${escapeHtml(c.commentText)}</div><div class="mt-2 d-flex align-items-center gap-2">${c.priorityAction ? badge('PRIORITY') : ''}<button type="button" class="btn btn-sm btn-outline-danger" data-delete-comment="${escapeHtml(c.commentId)}">Delete</button></div></div>`).join('') : '<p class="muted mb-0">No comments.</p>';
}
async function searchInvoices(){
  const ids=['invoiceNumber','shipmentName','customerName','invoiceStatus','invoiceType'];
  const fieldIds=['filterInvoiceNumber','filterShipmentName','filterCustomerName','filterInvoiceStatus','filterInvoiceType'];
  const criteria=Object.fromEntries(ids.map((key,index)=>[key,$(fieldIds[index]).value.trim() || null]));
  const data=await request('/api/invoices/search',jsonOptions('POST',criteria));
  $('invoiceRows').dataset.loaded='true';
  $('invoiceRows').innerHTML=data.length ? data.map(i=>`<tr><td><strong>${escapeHtml(i.invoiceNumber)}</strong></td><td>${escapeHtml(i.shipmentName)}</td><td>${escapeHtml(i.customerName)}</td><td>${badge(i.invoiceStatus)}</td><td>${escapeHtml(i.invoiceType)}</td></tr>`).join('') : '<tr><td colspan="5" class="muted">No matching invoices.</td></tr>';
}
async function loadInvoice(){
  const id=$('invoiceId').value.trim();if(!validUuid(id))throw new Error('Enter a valid invoice UUID.');
  const i=await request(`/api/invoices/${id}`);
  const fields=[['Invoice #',i.invoiceNumber],['Shipment',i.shipment?.shipmentName],['Customer',i.customer?.customerName],['Billing address',i.billingAddress],['Rate / km',i.ratePerKm],['Kilometers',i.kilometersSpent],['Taxes',i.taxes],['Additional charges',i.additionalCharges],['Total',i.totalAmount],['Status',i.invoiceStatus],['Type',i.invoiceType],['Issued at',i.issuedAt],['Issued by',i.issuedBy]];
  $('invoiceDetail').innerHTML=`<div class="row">${fields.map(([name,val])=>`<div class="col-md-6 mb-2"><div class="small muted">${escapeHtml(name)}</div><strong>${escapeHtml(val)}</strong></div>`).join('')}</div>`;
}
async function loadStatistics(){
  const data=await request('/statistics/getShipmentsCountByStatus');
  const lookup=new Map(data.map(x=>[x.status,x.count]));
  $('statsCards').innerHTML=STATUSES.map(status=>`<div class="col-sm-6 col-lg-4"><div class="card h-100"><div class="card-body"><div class="muted small mb-2">${escapeHtml(status.replaceAll('_',' '))}</div><div class="stat-value">${lookup.get(status) ?? 0}</div></div></div></div>`).join('');
}
async function startPdf(id){
  if(!validUuid(id))throw new Error('Enter a valid shipment UUID.');
  const jobId=await request(`/pdf/generatePdfAsync/${id}`,{method:'POST'},PDF_API);
  if(typeof jobId !== 'string' || !validUuid(jobId)) throw new Error('Unexpected job ID returned: '+JSON.stringify(jobId));
  state.jobs.unshift({id:jobId, shipmentId:id, status:'PENDING'});
  drawJobs();
  switchTab('pdf');
}
function drawJobs(){
  $('jobRows').innerHTML=state.jobs.length ? state.jobs.map(j=>`<tr><td>${uuid(j.id)}</td><td>${uuid(j.shipmentId)}</td><td>${badge(j.status)}</td></tr>`).join('') : '<tr><td colspan="3" class="muted">No jobs submitted yet.</td></tr>';
}
async function pollJobs(){
  if(state.polling) return;state.polling=true;
  try {
    await Promise.all(state.jobs.filter(j=>!['COMPLETED','FAILED'].includes(j.status)).map(async j=>{
      try { const result=await request(`/pdf/status/${j.id}`,{},PDF_API); j.status=result || 'UNKNOWN'; }
      catch(e) {j.status='ERROR';console.warn('Unable to poll PDF job:',e);}
    }));
    drawJobs();
  } finally {state.polling=false;}
}
function bind(){
  document.querySelectorAll('[data-tab]').forEach(btn=>btn.addEventListener('click',()=>run(()=>switchTab(btn.dataset.tab))));
  $('newStatus').innerHTML=STATUSES.map(s=>`<option value="${s}">${s.replaceAll('_',' ')}</option>`).join('');
  $('refreshShipments').addEventListener('click',()=>run(loadShipments));
  $('pageSize').addEventListener('change',()=>run(async()=>{state.size=Number($('pageSize').value);state.page=0;await loadShipments();}));
  $('prevPage').addEventListener('click',()=>run(async()=>{state.page--;await loadShipments();}));
  $('nextPage').addEventListener('click',()=>run(async()=>{state.page++;await loadShipments();}));
  $('shipmentRows').addEventListener('click',e=>{const btn=e.target.closest('[data-open-shipment]');if(btn) run(()=>openShipment(btn.dataset.openShipment));});
  $('lookupShipment').addEventListener('click',()=>run(()=>openShipment($('shipmentIdLookup').value.trim())));
  $('statusForm').addEventListener('submit',e=>{e.preventDefault();run(async()=>{if(!state.shipmentId)throw new Error('Select a shipment first.');await request(`/shipment/updateShipmentStatus/${state.shipmentId}`,jsonOptions('PATCH',$('newStatus').value));await loadShipments();await openShipment(state.shipmentId);},'Status updated.');});
  $('createForm').addEventListener('submit',e=>{e.preventDefault();run(async()=>{const response=await request('/shipment/createNewShipment',jsonOptions('POST',{shipmentName:$('newShipmentName').value.trim()}));$('newShipmentName').value='';state.page=0;await loadShipments();alertBox(String(response),'success');});});
  $('commentForm').addEventListener('submit',e=>{e.preventDefault();run(async()=>{if(!state.shipmentId)throw new Error('Select a shipment first.');const body={commentText:$('commentText').value.trim(),addedBy:$('commentAuthor').value.trim()||null,priorityAction:$('priorityAction').checked};await request(`/comment/addShipmentComment/${state.shipmentId}`,jsonOptions('POST',body));$('commentText').value='';await openShipment(state.shipmentId);},'Comment added.');});
  $('commentsList').addEventListener('click',e=>{const btn=e.target.closest('[data-delete-comment]');if(!btn)return;if(!confirm('Delete this comment?'))return;run(async()=>{await request(`/comment/deleteShipmentComment/${btn.dataset.deleteComment}`,{method:'DELETE'});await openShipment(state.shipmentId);},'Comment deleted.');});
  $('invoiceSearchForm').addEventListener('submit',e=>{e.preventDefault();run(searchInvoices);});
  $('loadInvoice').addEventListener('click',()=>run(loadInvoice));
  $('invoiceUpdateForm').addEventListener('submit',e=>{e.preventDefault();run(async()=>{const id=$('invoiceId').value.trim();if(!validUuid(id))throw new Error('Enter a valid invoice UUID.');const amount=Number($('invoiceAmount').value);if(!Number.isFinite(amount)||amount<0)throw new Error('Invalid amount.');await request(`/api/invoices/${id}/${$('invoiceField').value}`,jsonOptions('PATCH',amount));await loadInvoice();},'Invoice updated.');});
  $('refreshStats').addEventListener('click',()=>run(loadStatistics));
  $('pdfForm').addEventListener('submit',e=>{e.preventDefault();run(()=>startPdf($('pdfShipmentId').value.trim()));});
  $('pollNow').addEventListener('click',()=>run(pollJobs));
  $('kafkaForm').addEventListener('submit',e=>{e.preventDefault();run(()=>request(`/kafka/send?message=${encodeURIComponent($('kafkaMessage').value)}`,{method:'POST'}),'Kafka test message submitted.');});
  setInterval(()=>{if(state.jobs.some(j=>['PENDING','RUNNING'].includes(j.status)))pollJobs();},2500);
}
bind();
run(loadShipments);
