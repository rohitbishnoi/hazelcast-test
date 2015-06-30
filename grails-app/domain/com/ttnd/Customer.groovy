package com.ttnd

class Customer implements Serializable {

    String name
    Integer age
    static constraints = {
    }

    String toString() {
        "name:${name}, age:${age}"
    }
}

