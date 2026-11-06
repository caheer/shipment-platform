from pathlib import Path
from datetime import datetime

project_root = Path.cwd()

# Microservices that belong to the platform
services = [
    "shipment-service",
    "invoice-service",
    "pdf-service",
    "notification-service",
    "report-service",
]

# Files from the shipment-platform root directory
root_files = [
    "pom.xml",
    "docker-compose.yaml",
]

# Directories that should not appear in the snapshot
excluded_dirs = [
    project_root
    / "shipment-service"
    / "src"
    / "main"
    / "java"
    / "com"
    / "example"
    / "playground"
]

timestamp = datetime.now().strftime("%Y-%m-%d_%H-%M-%S")

output_file = (
    project_root
    / f"shipment_platform_project_snapshot_{timestamp}.txt"
)

print(f"Platform directory: {project_root}")


def is_excluded(file_path: Path) -> bool:
    for excluded_dir in excluded_dirs:
        if excluded_dir == file_path or excluded_dir in file_path.parents:
            return True

    return False


def write_file(output, file_path: Path):
    relative_path = file_path.relative_to(project_root)

    print(f"Adding: {relative_path}")

    output.write("\n")
    output.write("=" * 100 + "\n")
    output.write(f"FILE: {relative_path}\n")
    output.write("=" * 100 + "\n\n")

    try:
        content = file_path.read_text(encoding="utf-8")
        output.write(content)

        if not content.endswith("\n"):
            output.write("\n")

    except UnicodeDecodeError:
        output.write("[BINARY FILE SKIPPED]\n")


with output_file.open("w", encoding="utf-8") as output:

    # ------------------------------------------------------------------
    # Root platform files
    # ------------------------------------------------------------------

    for root_file_name in root_files:

        file_path = project_root / root_file_name

        if file_path.exists():
            write_file(output, file_path)
        else:
            print(f"WARNING: {root_file_name} not found")


    # ------------------------------------------------------------------
    # Microservices
    # ------------------------------------------------------------------

    for service_name in services:

        service_dir = project_root / service_name

        print()
        print(f"Processing service: {service_name}")

        if not service_dir.exists():
            print(f"WARNING: Service directory not found: {service_name}")
            continue


        # --------------------------------------------------------------
        # Service pom.xml
        # --------------------------------------------------------------

        service_pom = service_dir / "pom.xml"

        if service_pom.exists():
            write_file(output, service_pom)
        else:
            print(f"WARNING: pom.xml not found for {service_name}")


        # --------------------------------------------------------------
        # Service src/
        # --------------------------------------------------------------

        service_src = service_dir / "src"

        if not service_src.exists():
            print(f"WARNING: src directory not found for {service_name}")
            continue

        for file_path in sorted(service_src.rglob("*")):

            if not file_path.is_file():
                continue

            if is_excluded(file_path):
                print(
                    f"Skipping excluded file: "
                    f"{file_path.relative_to(project_root)}"
                )
                continue

            write_file(output, file_path)


print()
print("=" * 100)
print(f"DONE: {output_file}")
print("=" * 100)