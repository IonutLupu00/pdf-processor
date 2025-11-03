# Pdf-processor documentation

This is a spring boot project that exposes several REST endpoints for pdf processing. It uses qpdf and apache pdfbox for pdf operations.
Its features are still in progress. 
Currently supporting merge, split and several types of content extraction.

Tech stack: java 21, maven, zxing, apache pdfbox, qpdf.

Maven documentation https://maven.apache.org/guides/index.html

The app requires having qpdf in the system path. Qpdf is packaged inside the dockerfile of the project. 
Temporary folders are created inside the system tmp folder where the input files are downloaded. The folders get deleted after each operation is completed.

## 📄 Endpoints

## Split PDF

**POST** `/pdf/split`

Splits a PDF file according to the specified parameters.

#### Request body schema. Type: Multipart Form

| Field                  | Type   | Required | Description                                                                 |
|------------------------|--------|-----------|-----------------------------------------------------------------------------|
| `file`                 | binary | Yes      | Input PDF file                                                  |
| `splitType`            | enum   | Yes      | Accepted values: `PAGES_PER_FILE`, `TOTAL_FILES`, `PAGE_RANGES`, `FILE_SIZE`, `BOOKMARK`, `CODE` |
| `options`              | string | No       | Additional split options depending on `splitType`                            |

#### Response

- **200 OK**: Returns a `StreamingResponseBody` with a zip file containing the resulting pdf files.

### Split Options

The following split options are available for the PDF processing API:

#### PAGES_PER_FILE
- **Description**: Splits the PDF into multiple files, each containing a specified number of pages.
- **Usage**: `PAGES_PER_FILE=5`
- **Example**: To split a PDF into files with 5 pages each, use the option `PAGES_PER_FILE=5`.

#### TOTAL_FILES
- **Description**: Splits the PDF into a specified total number of files.
- **Usage**: `TOTAL_FILES=3`
- **Example**: To split a PDF into 3 files, use the option `TOTAL_FILES=3`.

#### BOOKMARK_TITLES
- **Description**: Splits the PDF at the specified bookmark titles.
- **Usage**: `BOOKMARK_TITLES=Chapter1;Chapter2`
- **Example**: To split a PDF at the bookmarks titled "Chapter1" and "Chapter2", use the option `BOOKMARK_TITLES=Chapter1;Chapter2`.

#### PAGE_RANGES
- **Description**: Split the PDF based on specified page ranges.
- **Usage**: Provide the page ranges in a format like `start-end`.
- **Example**: `PAGE_RANGES="1-5,7-10"`

#### FILE_SIZE_BYTES
- **Description**: Split the PDF into files, each not exceeding a specified size in bytes. Minimum document size is 1 page. In case the size limit provided is too small, 
the API will respond with the size in bytes of the part number it failed at - that part will be a single page that doesn't fit within the limit.
- **Usage**: Provide the maximum size in bytes as an integer.
- **Example**: `FILE_SIZE_BYTES=1000000`

---

### Merge PDF

**POST** `/pdf/merge`

Merges multiple PDF files into a single PDF.


#### Request body schema. Type: Multipart Form
| Field   | Type             | Required | Description                           |
|---------|-----------------|----------|---------------------------------------|
| `files` | array of binary  | Yes      | List of PDF files to merge (binary)   |


#### Response

- **200 OK**: Returns a `StreamingResponseBody` (merged PDF file).


### Extract content

#### Request body schema. Type: Multipart Form
| Field         | Type   | Required | Description                                                          |
|---------------|--------|----------|----------------------------------------------------------------------|
| `file`        | binary | Yes      | Input PDF file                                                       |
| `options`     | string | Yes      | Content extraction options                                           |
| `contentType` | enum   | no       | Accepted values: TEXT, IMAGE, METADATA, TABLE, FORM, ANNOTATION, OCR |

#### Text extraction
- **Description**: Extracts the text from each page. Each page outputs an individual .txt file.

#### Image extraction
- **Description**: Extracts the images from the whole document into individual jpg files.

## Useful commands

### Build the dockerfile

```bash

sudo docker build -t pdf-processor .
```

### Build the project

```bash

mvn clean install
```

### Run the docker container

```bash

sudo docker run -p 8081:8081 \
-v $(pwd)/src/main/resources/application.yml:/app/config/application.yml \
-v /home/john/temp/projectsTemp/pdfProcessor:/temp \
-e SPRING_PROFILES_ACTIVE=dev \
pdf-processor
```


## License
This project is licensed under the MIT License © 2025 [Ionut Lupu].
