from pathlib import Path
from datetime import datetime

project_root = Path.cwd()
src_dir = project_root / "src"

excluded_dir = (
    project_root
    / "src"
    / "main"
    / "java"
    / "com"
    / "example"
    / "playground"
)

# Example: project_snapshot_2026-09-21_17-02-15.txt
timestamp = datetime.now().strftime("%Y-%m-%d_%H-%M-%S")
output_file = project_root / f"project_snapshot_{timestamp}.txt"

print(f"Project directory: {project_root}")
print(f"Looking for src in: {src_dir}")
print(f"Excluding: {excluded_dir}")


def write_file(output, file_path):
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

    # Add pom.xml
    pom = project_root / "pom.xml"

    if pom.exists():
        write_file(output, pom)
    else:
        print("WARNING: pom.xml not found")

    # Add everything under src/, except playground
    if src_dir.exists():
        for file_path in sorted(src_dir.rglob("*")):

            if excluded_dir in file_path.parents:
                continue

            if file_path.is_file():
                write_file(output, file_path)

    else:
        print("WARNING: src directory not found")


print()
print(f"DONE: {output_file}")