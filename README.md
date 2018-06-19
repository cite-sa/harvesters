## Harvester
Harvester service offers the capability of harvesting various types of sources (WCS, OAI-PMH, OBIS) and storing data in FeMME.

Harvester runs in Java 8 and Tomcat 8. It uses MongoDB.

Harvester is packaged as a war file. It is configured by the following configuration properties.

#### harvester-application
    #MongoDB host:port
    gr.cite.femme.earthserver.harvester.datastore.mongodb.host=localhost:27017
    #Harvester DB name
    gr.cite.femme.earthserver.harvester.datastore.mongodb.name=harvester-db

    #Max harvests logged
    gr.cite.femme.earthserver.harvester.harvests.maxLoggedHarvestCycles=5
    #Period to check for changes in harvest tasks
    gr.cite.femme.earthserver.harvester.cycle.milliseconds=60000

    #FeMME service endpoint
    gr.cite.femme.earthserver.wcsAdapter.femme.url=http://localhost:8081/femme
    #Geospatial service endpoint
    gr.cite.femme.earthserver.wcsAdapter.femme.geo.url=http://localhost:8081/femme-geo
