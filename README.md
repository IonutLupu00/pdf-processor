# Pdf-processor documentation

This is a spring boot project that exposes several REST endpoints for pdf processing. It uses qpdf and apache pdfbox for pdf operations.
Its features are still in progress. 
Currently supporting merge, split and several types of content extraction.

It uses maven as the project build framework. 
Maven documentation https://maven.apache.org/guides/index.html

The app requires to have qpdf in the system path. Qpdf is packaged inside the dockerfile of the project.

## Supported operations

Some operations support passing the request attribute 'options', which is usually required and directs the behavior, for example which pages ranges to split by.

### Split

All pdf split services use PdfSplitter. Pdf splitter splits by page ranges using qpdf. Every split type does its unique calculations to compute the page ranges which are passed to PdfSplitter.

### Merge

Single merge service, which uses PdfMerger. Pdf merger uses qpdf to merge pdfs. It merges the pdfs in the order they are passed to it.

### Content extraction

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
