# Element seeker Application
Currently supports only XML formats as an input.
# Example cmd line
    java -jar target\test-1.0-SNAPSHOT.jar -o "D:\sample-0-origin.html" -c "D:\sample-1-evil-gemini.html"
Or simply run --help to see useful comman list

    java -jar target\test-1.0-SNAPSHOT.jar --help
# Implementation details
Has two core interfaces which allow to tune implementation later on for other file formats or even some network data:
1. com.agileengine.test.ElementByIdSeekerService
2. com.agileengine.test.SimilarElementSeekerService

Current XML file to XML file seeking "configuration" is hidden under following class:

    com.agileengine.test.xml.XMLComparatorService