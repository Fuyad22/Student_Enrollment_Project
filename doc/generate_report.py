import os
import matplotlib.pyplot as plt
import matplotlib.patches as patches
from xhtml2pdf import pisa

def draw_erd():
    print("Generating ERD image using matplotlib...")
    fig, ax = plt.subplots(figsize=(10, 6))
    ax.set_xlim(0, 10)
    ax.set_ylim(0, 6)
    ax.axis('off')

    # Function to draw entity box
    def draw_entity(ax, x, y, w, h, title, fields):
        # Header box (blue)
        header = patches.Rectangle((x, y + h - 0.7), w, 0.7, facecolor='#2980b9', edgecolor='black', linewidth=1.5)
        ax.add_patch(header)
        # Body box (light grey/white)
        body = patches.Rectangle((x, y), w, h - 0.7, facecolor='#f8f9fa', edgecolor='black', linewidth=1.5)
        ax.add_patch(body)
        
        # Title Text
        ax.text(x + w/2, y + h - 0.35, title, color='white', weight='bold', fontsize=11, ha='center', va='center')
        
        # Fields Text
        y_offset = y + h - 1.1
        for field in fields:
            ax.text(x + 0.2, y_offset, field, fontsize=9.5, ha='left', va='center', family='monospace')
            y_offset -= 0.35

    # Draw Student Entity
    draw_entity(ax, 0.5, 2.5, 2.8, 2.8, "STUDENT (Entity 1)", [
        "student_id (PK)",
        "name",
        "email",
        "gender (Enum)",
        "enrollment_date"
    ])

    # Draw Unit Entity
    draw_entity(ax, 6.7, 2.5, 2.8, 2.5, "UNIT (Entity 2)", [
        "unit_code (PK)",
        "title",
        "credits",
        "department"
    ])

    # Draw Timetable Link Entity
    draw_entity(ax, 3.6, 0.3, 2.8, 2.9, "TIMETABLE (Link Entity)", [
        "timetable_id (PK)",
        "student_id (FK)",
        "unit_code (FK)",
        "class_day (Enum)",
        "class_time"
    ])

    # Draw connecting lines with relations
    # Line from Student to Timetable
    ax.plot([1.9, 1.9, 3.6], [2.5, 1.2, 1.2], color='#2c3e50', linewidth=1.5)
    # Add "1" and "N" notation
    ax.text(2.1, 2.3, "1", fontsize=11, weight='bold', color='#e74c3c')
    ax.text(3.3, 1.4, "N", fontsize=11, weight='bold', color='#e74c3c')

    # Line from Unit to Timetable
    ax.plot([8.1, 8.1, 6.4], [2.5, 1.2, 1.2], color='#2c3e50', linewidth=1.5)
    # Add "1" and "N" notation
    ax.text(7.8, 2.3, "1", fontsize=11, weight='bold', color='#e74c3c')
    ax.text(6.7, 1.4, "N", fontsize=11, weight='bold', color='#e74c3c')

    plt.tight_layout()
    plt.savefig("doc/erd.png", dpi=300, bbox_inches='tight')
    plt.close()
    print("ERD image successfully saved to doc/erd.png.")

def generate_pdf():
    print("Compiling project report to PDF using xhtml2pdf...")
    
    html_content = """
    <!DOCTYPE html>
    <html>
    <head>
    <style>
        @page {
            size: a4;
            margin: 2.2cm;
        }
        body {
            font-family: "Times New Roman", Times, Georgia, serif;
            font-size: 10.5pt;
            color: #111111;
            line-height: 1.45;
            text-align: justify;
        }
        .title {
            font-size: 18pt;
            font-weight: bold;
            text-align: center;
            margin-top: 10px;
            margin-bottom: 4px;
            color: #000000;
        }
        .subtitle {
            font-size: 12pt;
            text-align: center;
            margin-bottom: 25px;
            font-style: italic;
            color: #333333;
        }
        .author-table {
            width: 100%;
            border-collapse: collapse;
            margin-bottom: 20px;
            border-top: none !important;
            border-bottom: none !important;
        }
        .author-table td {
            border: none !important;
            padding: 4px;
            vertical-align: top;
        }
        .author-details {
            font-size: 10pt;
            line-height: 1.4;
        }
        .photo-box {
            width: 95px;
            height: 115px;
            border: 1px solid #7f8c8d;
        }
        .abstract-container {
            margin: 20px 30px;
            padding: 10px 0;
            border-top: 0.5pt solid #777777;
            border-bottom: 0.5pt solid #777777;
        }
        .abstract-title {
            font-weight: bold;
            font-size: 10pt;
            text-align: center;
            margin-bottom: 5px;
        }
        .abstract-text {
            font-size: 9.5pt;
            font-style: italic;
            text-align: justify;
            line-height: 1.35;
        }
        h1 {
            font-size: 13pt;
            font-weight: bold;
            color: #000000;
            margin-top: 22pt;
            margin-bottom: 8pt;
            border-bottom: 0.5pt solid #000000;
            padding-bottom: 3px;
        }
        h2 {
            font-size: 11pt;
            font-weight: bold;
            color: #000000;
            margin-top: 16pt;
            margin-bottom: 6pt;
        }
        h3 {
            font-size: 10pt;
            font-weight: bold;
            font-style: italic;
            color: #000000;
            margin-top: 12pt;
            margin-bottom: 4pt;
        }
        table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 12px;
            margin-bottom: 12px;
            border-top: 1.5pt solid #000000;
            border-bottom: 1.5pt solid #000000;
        }
        th, td {
            padding: 6px 10px;
            text-align: left;
            font-size: 9.5pt;
            border: none;
        }
        th {
            font-weight: bold;
            border-bottom: 0.8pt solid #000000;
            background-color: transparent;
            color: #000000;
        }
        td {
            border-bottom: 0.5pt solid #eeeeee;
        }
        tr:last-child td {
            border-bottom: none;
        }
        .erd-img {
            display: block;
            width: 75%;
            margin: 15px auto;
            border: 0.5pt solid #7f8c8d;
        }
        .code {
            font-family: "Courier New", Courier, monospace;
            font-size: 9pt;
            font-weight: bold;
        }
        .bullet-list {
            margin-top: 5px;
            margin-bottom: 10px;
            padding-left: 20px;
        }
        .bullet-list li {
            margin-bottom: 4px;
        }
    </style>
    </head>
    <body>
        
        <!-- LATEX-STYLE HEADING BLOCK WITH TWO-COLUMN AUTHOR SECTION -->
        <div class="title">Student Enrollment &amp; Timetable System</div>
        <div class="subtitle">Object-Oriented Programming (OOP) Final Project Report</div>
        
        <table class="author-table">
            <tr>
                <td style="width: 75%;">
                    <div class="author-details">
                        <strong>Author:</strong> Md Fuyad Hassan (ID: 232-134-043)<br>
                        <strong>Affiliation:</strong> Dept. of Software Engineering, Metropolitan University<br>
                        <strong>Course:</strong> Object Oriented Programming (OOP)<br>
                        <strong>Development Stack:</strong> Java GUI (Swing) &amp; SQLite RDBMS via JDBC<br>
                        <strong>Submission Date:</strong> August 15, 2026
                    </div>
                </td>
                <td style="width: 25%; text-align: right;">
                    <img class="photo-box" src="doc/photo.jpg" alt="Author Photograph" />
                </td>
            </tr>
        </table>
        
        <!-- ABSTRACT SECTION -->
        <div class="abstract-container">
            <div class="abstract-title">Abstract</div>
            <div class="abstract-text">
                This report details the implementation of an integrated desktop platform designed to manage student registration and academic course scheduling. Built using Java Swing GUI controls and a relational SQLite database connected through JDBC, the application implements a normalized schema designed to resolve a classic many-to-many relationship using a junction timetable entity. In addition, the development incorporates a custom Date validation component, an interactive visual date picker, custom Java enums mapped to user controls, and database cascading operations to maintain relational integrity. The design adheres strictly to standard Object-Oriented Programming principles and enterprise naming conventions.
            </div>
        </div>
        
        <!-- SECTION 1 -->
        <h1>1. Project Description</h1>
        <p>
            The <strong>Student Enrollment and Timetable Management System</strong> is a desktop software application developed to manage academic student enrollments and course registration details. The application redevelops the architectural concepts originally implemented in a Python-based GUI database project into a type-safe, compiled Java Swing environment.
        </p>
        <p>
            From a database modeling perspective, the relationship between Students and course Units is inherently <strong>many-to-many</strong>: a single student can enroll in multiple units, and an individual unit can contain numerous registered students. To represent this structure in an RDBMS, the <strong>Timetable</strong> entity serves as a <strong>Link Entity</strong> (junction table). Each record in the timetable table links a unique student record with a corresponding course unit code, while scheduling day and time attributes are mapped directly to this linkage.
        </p>
        <p>
            The software design incorporates several critical components to satisfy OOP guidelines:
        </p>
        <ul class="bullet-list">
            <li><strong>Java Swing Desktop GUI:</strong> A multi-tab dashboard (<code>JTabbedPane</code>) that integrates three independent forms for Students, Units, and Schedules. Each form is paired with an interactive <code>JTable</code> representing the live state of the underlying SQLite tables.</li>
            <li><strong>Custom Date Implementation:</strong> To demonstrate standard calendar logic, a custom <code>MyDate</code> class is written. It encapsulates year, month, and day integer fields, and implements leap-year calculations and monthly day-bound validations to restrict invalid object instantiations.</li>
            <li><strong>Interactive Date Picker:</strong> To enter date information visually, a custom, grid-based modal dialog (<code>DatePickerDialog</code>) is built in Swing. Users navigate months/years and click days to return validated <code>MyDate</code> objects.</li>
            <li><strong>Enum Types:</strong> Java <code>enum</code> structures (<code>Gender</code> and <code>DayOfWeek</code>) are implemented for categorical fields, ensuring compile-time safety. In the UI, these are represented using a <code>ButtonGroup</code> of radio buttons for gender and a dropdown <code>JComboBox</code> for timetable days.</li>
            <li><strong>Relational Database Cascade:</strong> To prevent orphaned records, foreign key cascade actions are established. If a student or course unit record is deleted, the RDBMS automatically purges all related schedule entries in the timetable link table.</li>
        </ul>
        
        <!-- SECTION 2 -->
        <h1>2. Entity Relationship Diagram (ERD)</h1>
        <p>
            The Entity Relationship Diagram (ERD) defines the schema structure, attributes, and cardinality of the three entities. The central <strong>TIMETABLE</strong> entity functions as the link table resolving the many-to-many relationship:
        </p>
        <img class="erd-img" src="doc/erd.png" alt="Entity Relationship Diagram" />
        <p>
            <strong>Cardinality Specifications:</strong>
        </p>
        <ul class="bullet-list">
            <li>One <strong>STUDENT</strong> is mapped to zero, one, or many scheduling entries in the <strong>TIMETABLE</strong> table (1 to N relationship). Deleting a student cascades and purges matching schedule entries.</li>
            <li>One <strong>UNIT</strong> is mapped to zero, one, or many scheduling entries in the <strong>TIMETABLE</strong> table (1 to N relationship). Deleting a unit cascades and purges matching schedule entries.</li>
        </ul>
        
        <!-- SECTION 3 -->
        <h1 style="page-break-before: always;">3. Database Relational Schema</h1>
        <p>
            The SQLite database structure contains three relational tables. The field-level structures and data constraints are specified in the following tables:
        </p>
        
        <h2>3.1 Table: students</h2>
        <table>
            <thead>
                <tr>
                    <th>Field Name</th>
                    <th>Data Type</th>
                    <th>Constraints</th>
                    <th>Description</th>
                </tr>
            </thead>
            <tbody>
                <tr>
                    <td class="code">id</td>
                    <td>INTEGER</td>
                    <td>PRIMARY KEY</td>
                    <td>Unique Student ID number</td>
                </tr>
                <tr>
                    <td class="code">name</td>
                    <td>TEXT</td>
                    <td>NOT NULL</td>
                    <td>Full name of the student</td>
                </tr>
                <tr>
                    <td class="code">email</td>
                    <td>TEXT</td>
                    <td>NOT NULL</td>
                    <td>Student's email address</td>
                </tr>
                <tr>
                    <td class="code">gender</td>
                    <td>TEXT</td>
                    <td>NOT NULL</td>
                    <td>Enum value (persisted as MALE, FEMALE, or OTHER)</td>
                </tr>
                <tr>
                    <td class="code">enrollment_date</td>
                    <td>TEXT</td>
                    <td>NOT NULL</td>
                    <td>Admission date (persisted as string in format YYYY-MM-DD)</td>
                </tr>
            </tbody>
        </table>

        <h2>3.2 Table: units</h2>
        <table>
            <thead>
                <tr>
                    <th>Field Name</th>
                    <th>Data Type</th>
                    <th>Constraints</th>
                    <th>Description</th>
                </tr>
            </thead>
            <tbody>
                <tr>
                    <td class="code">unit_code</td>
                    <td>TEXT</td>
                    <td>PRIMARY KEY</td>
                    <td>Alphanumeric course code (e.g., CSE101)</td>
                </tr>
                <tr>
                    <td class="code">title</td>
                    <td>TEXT</td>
                    <td>NOT NULL</td>
                    <td>Official course title</td>
                </tr>
                <tr>
                    <td class="code">credits</td>
                    <td>INTEGER</td>
                    <td>NOT NULL</td>
                    <td>Credit hour value (1 to 10)</td>
                </tr>
                <tr>
                    <td class="code">department</td>
                    <td>TEXT</td>
                    <td>NOT NULL</td>
                    <td>Academic department offering the unit</td>
                </tr>
            </tbody>
        </table>

        <h2>3.3 Table: timetable (Junction / Link Table)</h2>
        <table>
            <thead>
                <tr>
                    <th>Field Name</th>
                    <th>Data Type</th>
                    <th>Constraints</th>
                    <th>Description</th>
                </tr>
            </thead>
            <tbody>
                <tr>
                    <td class="code">timetable_id</td>
                    <td>INTEGER</td>
                    <td>PRIMARY KEY AUTOINCREMENT</td>
                    <td>Unique schedule record ID</td>
                </tr>
                <tr>
                    <td class="code">student_id</td>
                    <td>INTEGER</td>
                    <td>FOREIGN KEY -> students(id) ON DELETE CASCADE</td>
                    <td>Reference link to student</td>
                </tr>
                <tr>
                    <td class="code">unit_code</td>
                    <td>TEXT</td>
                    <td>FOREIGN KEY -> units(unit_code) ON DELETE CASCADE</td>
                    <td>Reference link to course unit</td>
                </tr>
                <tr>
                    <td class="code">class_day</td>
                    <td>TEXT</td>
                    <td>NOT NULL</td>
                    <td>Scheduled class day (Enum value: MONDAY, etc.)</td>
                </tr>
                <tr>
                    <td class="code">class_time</td>
                    <td>TEXT</td>
                    <td>NOT NULL</td>
                    <td>Time range details (e.g., 09:00 AM - 10:30 AM)</td>
                </tr>
            </tbody>
        </table>

        <!-- SECTION 4 -->
        <h1>4. Object-Oriented Principles &amp; Design Decisions</h1>
        <p>
            The project design implements core Object-Oriented Programming (OOP) paradigms:
        </p>
        <ul class="bullet-list">
            <li><strong>Encapsulation:</strong> Entity attributes (in <code>Student</code>, <code>Unit</code>, and <code>Timetable</code> classes) are marked as <code>private</code>. Access is mediated through public getters and setters, ensuring strict state control.</li>
            <li><strong>Validation &amp; State Invariant Protection:</strong> The custom <code>MyDate</code> class protects its date invariant by validating fields in constructors and setters:
                <pre class="code">public MyDate(int day, int month, int year) {
    if (!isValidDate(day, month, year)) {
        throw new IllegalArgumentException("Invalid date");
    }
    // ...
}</pre>
            </li>
            <li><strong>Separation of Concerns:</strong> SQL interactions are segregated in [`DatabaseManager.java`](file:///e:/SWE/2.1 semester/OOP/Project1/src/dbms/DatabaseManager.java), while business logic remains in the model classes, and user presentation remains in the Swing frame.</li>
            <li><strong>Code Conventions:</strong> Class names use UpperCamelCase, variables use lowerCamelCase, database tables use snake_case, and SQL keywords are kept in UPPERCASE to maintain strict readability.</li>
        </ul>
    </body>
    </html>
    """
    
    output_filename = "doc/Md_Fuyad_Hassan_Project_Report.pdf"
    
    with open(output_filename, "w+b") as result_file:
        pisa_status = pisa.CreatePDF(html_content, dest=result_file)
        
    if not pisa_status.err:
        print(f"PDF Report generated successfully at: {output_filename}")
    else:
        print("Error generating PDF!")

if __name__ == "__main__":
    # Ensure doc directory exists
    if not os.path.exists("doc"):
        os.makedirs("doc")
    draw_erd()
    generate_pdf()
