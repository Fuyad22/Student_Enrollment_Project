import os
import re
from xhtml2pdf import pisa

def latex_to_html(tex_content):
    # Extract metadata
    title_match = re.search(r'\\title\{(.+?)\}', tex_content, re.DOTALL)
    author_match = re.search(r'\\author\{(.+?)\}', tex_content, re.DOTALL)
    date_match = re.search(r'\\date\{(.+?)\}', tex_content, re.DOTALL)
    abstract_match = re.search(r'\\begin\{abstract\}(.+?)\\end\{abstract\}', tex_content, re.DOTALL)

    title = title_match.group(1).replace('\\textbf{', '').replace('}', '').replace('\\', '<br>') if title_match else "User Manual"
    author = author_match.group(1).replace('\\textbf{', '').replace('}', '') if author_match else ""
    date = date_match.group(1) if date_match else ""
    abstract = abstract_match.group(1).strip() if abstract_match else ""

    # Get body (content between \begin{document} and \end{document})
    body_match = re.search(r'\\begin\{document\}(.+?)\\end\{document\}', tex_content, re.DOTALL)
    if not body_match:
        return "Invalid LaTeX document"
    
    body = body_match.group(1)
    
    # Strip metadata macros from body
    body = re.sub(r'\\maketitle', '', body)
    body = re.sub(r'\\begin\{abstract\}.+?\\end\{abstract\}', '', body, flags=re.DOTALL)

    # Basic LaTeX to HTML replacements
    
    # Sections
    body = re.sub(r'\\section\{(.+?)\}', r'<h2>\1</h2>', body)
    body = re.sub(r'\\subsection\{(.+?)\}', r'<h3>\1</h3>', body)
    body = re.sub(r'\\subsubsection\{(.+?)\}', r'<h4>\1</h4>', body)

    # Text formatting
    body = re.sub(r'\\textbf\{(.+?)\}', r'<strong>\1</strong>', body)
    body = re.sub(r'\\texttt\{(.+?)\}', r'<code class="inline-code">\1</code>', body)

    # Lists
    # Itemize
    body = re.sub(r'\\begin\{itemize\}', r'<ul>', body)
    body = re.sub(r'\\end\{itemize\}', r'</ul>', body)
    # Enumerate
    body = re.sub(r'\\begin\{enumerate\}', r'<ol>', body)
    body = re.sub(r'\\end\{enumerate\}', r'</ol>', body)
    # Description
    body = re.sub(r'\\begin\{description\}', r'<dl>', body)
    body = re.sub(r'\\end\{description\}', r'</dl>', body)
    
    # Items
    body = re.sub(r'\\item\[(.+?)\]', r'<dt><strong>\1</strong></dt><dd>', body)
    # Closing description dt/dd
    body = re.sub(r'\\item', r'</li><li>', body)
    
    # Clean lists tags
    body = body.replace('<ul></li><li>', '<ul><li>')
    body = body.replace('<ol></li><li>', '<ol><li>')
    body = body.replace('</li><li></ul>', '</li></ul>')
    body = body.replace('</li><li></ol>', '</li></ol>')
    # Close description list mappings
    # Simple regex fix for description lists: convert </dl> items
    body = re.sub(r'<dd>(.*?)(?=<dt>|<dl>|</dl>)', r'<dd>\1</dd>', body, flags=re.DOTALL)
    
    # Code Blocks (lstlisting)
    def code_repl(match):
        code = match.group(1).strip()
        code = code.replace('<', '&lt;').replace('>', '&gt;')
        return f'<pre class="code-block">{code}</pre>'
    body = re.sub(r'\\begin\{lstlisting\}.*?\}(.*?)\\end\{lstlisting\}', code_repl, body, flags=re.DOTALL)
    body = re.sub(r'\\begin\{lstlisting\}(.*?)\\end\{lstlisting\}', code_repl, body, flags=re.DOTALL)

    # HTML Output
    html = f"""<!DOCTYPE html>
    <html>
    <head>
    <meta charset="utf-8">
    <style>
        @page {{
            size: a4;
            margin: 2.5cm;
        }}
        body {{
            font-family: "Times New Roman", Times, Georgia, serif;
            font-size: 11pt;
            color: #111111;
            line-height: 1.6;
            text-align: justify;
        }}
        .header {{
            text-align: center;
            margin-bottom: 25px;
        }}
        .title {{
            font-size: 18pt;
            font-weight: bold;
            margin-bottom: 8px;
        }}
        .author {{
            font-size: 12pt;
            margin-bottom: 4px;
        }}
        .date {{
            font-size: 11pt;
            margin-bottom: 15px;
            color: #555555;
        }}
        .divider {{
            border-top: 0.5px solid #666666;
            margin-bottom: 25px;
        }}
        .abstract {{
            margin: 0 20px 30px 20px;
            font-style: italic;
            font-size: 10pt;
            text-align: justify;
        }}
        .abstract-title {{
            font-weight: bold;
            text-align: center;
            margin-bottom: 5px;
            font-style: normal;
        }}
        h2 {{
            font-size: 14pt;
            font-weight: bold;
            margin-top: 25px;
            margin-bottom: 10px;
            border-bottom: 0.5px solid #dddddd;
            padding-bottom: 3px;
        }}
        h3 {{
            font-size: 12pt;
            font-weight: bold;
            margin-top: 18px;
            margin-bottom: 8px;
        }}
        h4 {{
            font-size: 11pt;
            font-weight: bold;
            font-style: italic;
            margin-top: 15px;
            margin-bottom: 5px;
        }}
        ul, ol {{
            margin-top: 5px;
            margin-bottom: 15px;
            padding-left: 20px;
        }}
        li {{
            margin-bottom: 5px;
        }}
        dl {{
            margin-top: 5px;
            margin-bottom: 15px;
        }}
        dt {{
            font-weight: bold;
            margin-top: 10px;
        }}
        dd {{
            margin-left: 20px;
            margin-bottom: 10px;
        }}
        .inline-code {{
            font-family: "Courier New", Courier, monospace;
            background-color: #f5f5f5;
            padding: 1px 3px;
            font-size: 10pt;
        }}
        .code-block {{
            font-family: "Courier New", Courier, monospace;
            font-size: 9.5pt;
            background-color: #f7f7f7;
            border: 0.5px solid #cccccc;
            padding: 8px 12px;
            margin: 15px 0;
            line-height: 1.3;
        }}
    </style>
    </head>
    <body>
        <div class="header">
            <div class="title">{title}</div>
            <div class="author">{author}</div>
            <div class="date">{date}</div>
        </div>
        <div class="divider"></div>
        
        {f'<div class="abstract"><div class="abstract-title">Abstract</div>{abstract}</div>' if abstract else ''}
        
        <div class="content">
            {body}
        </div>
    </body>
    </html>
    """
    return html

def main():
    tex_file = "user_manual.tex"
    pdf_file = "user_manual.pdf"
    
    if not os.path.exists(tex_file):
        print(f"Error: {tex_file} not found.")
        return
        
    print(f"Reading {tex_file}...")
    with open(tex_file, "r", encoding="utf-8") as f:
        tex_content = f.read()
        
    print("Converting LaTeX to HTML...")
    html_content = latex_to_html(tex_content)
    
    print(f"Generating PDF {pdf_file}...")
    with open(pdf_file, "wb") as f_pdf:
        pisa_status = pisa.CreatePDF(html_content, dest=f_pdf)
        
    if pisa_status.err:
        print("Error: PDF generation failed!")
    else:
        print(f"Success! PDF generated successfully at: {os.path.abspath(pdf_file)}")

if __name__ == '__main__':
    main()
