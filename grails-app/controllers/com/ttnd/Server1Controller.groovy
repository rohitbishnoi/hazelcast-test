package com.ttnd

import hazelgrails.HazelService

class Server1Controller {
    HazelService hazelService

    def index = {
        def cs = new Customer()
        cs.name = "tom"
        cs.age = 20
        def customers = hazelService.queue("customers")
        customers << cs

        def cities = hazelService.map("cities")
        cities["1"] = "London"
        cities.put("2","New York")

        def timestamps = hazelService.hashset("timestamps")
        timestamps << System.currentTimeMillis()
        render "Cities:"+ cities + " <br/> Timestamps:" + timestamps
    }
}
