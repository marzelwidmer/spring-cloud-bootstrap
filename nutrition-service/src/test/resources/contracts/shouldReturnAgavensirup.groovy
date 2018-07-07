import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "should return Agavensirup"
    request {
        method GET()
        url("/foods") {
            queryParameters {
                parameter("name", "Agavensirup")
            }
        }
    }
    response {
        String response = "{\n" +
                "    \"totalItems\": 1,\n" +
                "    \"_embedded\": {\n" +
                "        \"foods\": [\n" +
                "            {\n" +
                "                \"name\": \"Agavensirup\",\n" +
                "                \"_links\": {\n" +
                "                    \"self\": {\n" +
                "                        \"href\": \"http://localhost:4002/foods/5b3877a712f6466db803da0e\"\n" +
                "                    }\n" +
                "                }\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    \"_links\": {\n" +
                "        \"self\": {\n" +
                "            \"href\": \"http://localhost:4002/foods\"\n" +
                "        },\n" +
                "        \"find\": {\n" +
                "            \"href\": \"http://localhost:4002/foods{?name}\",\n" +
                "            \"templated\": true\n" +
                "        }\n" +
                "    }\n" +
                "}"
        String json = "{\n" +
                "  \"_embedded\" : {\n" +
                "    \"foods\" : [ ]\n" +
                "  },\n" +
                "  \"_links\" : {\n" +
                "    \"self\" : {\n" +
                "      \"href\" : \"http://localhost/foods{?page,size,sort}\",\n" +
                "      \"templated\" : true\n" +
                "    },\n" +
                "    \"profile\" : {\n" +
                "      \"href\" : \"http://localhost/profile/foods\"\n" +
                "    },\n" +
                "    \"search\" : {\n" +
                "      \"href\" : \"http://localhost/foods/search\"\n" +
                "    }\n" +
                "  },\n" +
                "  \"page\" : {\n" +
                "    \"size\" : 20,\n" +
                "    \"totalElements\" : 0,\n" +
                "    \"totalPages\" : 0,\n" +
                "    \"number\" : 0\n" +
                "  }\n" +
                "}"

        body(json)
        status 200
    }
}

