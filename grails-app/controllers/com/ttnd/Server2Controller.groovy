package com.ttnd

import hazelgrails.HazelService

class Server2Controller {
    HazelService hazelService

    def index = {
        def cs = hazelService.queue("customers").peek()
        def cities = hazelService.map("cities")
        def timestamps = hazelService.hashset("timestamps")
        render "Cities:"+ cities +" <br/> First customer " + cs + " <br/> Timestamps:" + timestamps
    }

}
