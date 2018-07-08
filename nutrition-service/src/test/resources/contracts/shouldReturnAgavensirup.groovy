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
        headers {
            contentType('application/hal+json;charset=UTF-8')
        }
        status 200
        body(
                _links: [
                        self: [
                                href: value(
                                        consumer("http://${fromRequest().header('Host')}${fromRequest().url()}"),
                                        producer("http://localhost${fromRequest().path()}")
                                )
                        ],
                        'find' : [
                                href: value(
                                        consumer("http://${fromRequest().header('Host')}/foods/{name}"),
                                        producer("http://localhost/foods{?name}")
//                                        producer("http://${fromRequest().header('Host')}/foods/{name}")
                                ),
                                templated: true
                        ]
                ],
                _embedded: [
                    foods : [

                    ]
                ],
                totalItems : 0
        )
    }
}

