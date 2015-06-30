package com.ttnd

import com.hazelcast.core.Hazelcast
import com.hazelcast.core.ITopic
import com.hazelcast.core.Message
import com.hazelcast.core.MessageListener
import hazelgrails.HazelService

import java.util.concurrent.TimeUnit

class UsagesController {

    HazelService hazelService

    def saveGet = {
        def customer = new Customer(name: "John", age: 27)
        customer.saveHz()
        println "Customer id:"+ customer.id
        println "Customer:" + Customer.getHz(customer.id)
        render "success"
    }

    def mapOperations = {
        def map = hazelService.map("customers")
        map.put("john", new Customer(name: "John", age: 27))
        // try putting with timeout
        map.tryPut("alex", new Customer(name: "Alex", age: 23), 1, TimeUnit.SECONDS)
        // put object with TTL (time to live)
        map.put("mary", new Customer(name: "Mary", age: 45), 100, TimeUnit.SECONDS)
        println map.size()
        println map["mary"].age

        // traverse the instances stored on current node
        map.localKeySet().each {
            println map[it].name
        }
        // traverse all objects
        map.values.each {
            println it
        }
    }

    def setOperations = {
        def cset = hazelService.hashset("customers")
        def customer = new Customer(name: "John", age: 27)
        cset << customer
        cset.add(customer)
        println cset.size()
        println cset.contains(customer)
        render "success"
    }


    def queueOperations = {
        def q = hazelService.queue("customers")
        def c1 = new Customer(name: "John", age: 27)
        def c2 = new Customer(name: "Alex", age: 23)
        def c3 = new Customer(name: "Mary", age: 45)
        q << c1
        q.put(c2)
        q.offer(c3)
        println "Queue size:" + q.size()
        println "First customer:" + q.poll()
        println "Second customer:" + q.poll(500, TimeUnit.MILLISECONDS)
        println "Last customer: (not removed)" + q.peek()
        println "Queue size:" + q.size()
        render "success"
    }

    def listOperations = {
        def list = hazelService.list("customerlist")
        def c1 = new Customer(name: "John", age: 27)
        def c2 = new Customer(name: "Alex", age: 23)
        def c3 = new Customer(name: "Mary", age: 45)
        list << c1
        list.add(c2)
        list.add(c3)

        println "List size:" + list.size()
        println "First customer:" + list[0]
        list.remove(c3)
        println "List size:" + list.size()
        render "success"
    }

    def lockOperations = {
        // cluster-wide lock
        def lock = hazelService.lock(new Sample());
        lock.lock();
        try {
            // do something here
        } finally {
            lock.unlock();
        }
        render "success"
    }

    def idOperations = {
        // cluster-wide id generation
        println "generated id:" + hazelService.generateId("customer")
        println "generated id:" + hazelService.generateId("customer")
        println "generated id:" + hazelService.generateId("customer")
        render "success"
    }

    def topicOperations = {
        def sample = new Sample();
        ITopic topic = hazelService.topic("default");
        topic.addMessageListener(sample);
        topic.publish (new Message<String>("mytopic", "ping"));
        render "success"
    }

    class Sample implements MessageListener<Message>, Serializable {
        public void onMessage(Message msg) {
            System.out.println("Message received");
        }
    }

    def hazelcastOperations = {
        def map = hazelService.map("customers")

        println map.size()
        // print local member
        println Hazelcast.cluster.localMember
        // print each member
        Hazelcast.cluster.members.each {
            println it
        }

        // create another hazelcast instance
        Hazelcast.newHazelcastInstance(null)

        // restart instance
        Hazelcast.getLifecycleService().restart()

        // shutdown cluster
        Hazelcast.shutdownAll()
        render "success"
    }

}
