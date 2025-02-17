/*
Copyright 2020 RethinkDNS and its authors

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

https://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package com.celzero.bravedns.database

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.celzero.bravedns.net.doh.Transaction
import com.celzero.bravedns.util.Constants
import com.celzero.bravedns.util.Constants.Companion.INIT_TIME_MS
import java.text.SimpleDateFormat
import java.util.*

// TODO: Indexes for responseIps
@Entity(tableName = "DnsLogs", indices = [Index(value = arrayOf("queryStr"), unique = false)])
class DnsLog {

    @PrimaryKey(autoGenerate = true) var id: Int = 0
    var queryStr: String = ""
    var time: Long = INIT_TIME_MS
    var flag: String = ""
    var resolver: String = ""
    var latency: Long = 0L
    var typeName: String = ""
    var isBlocked: Boolean = false
    var blockLists: String = ""
    var serverIP: String = ""
    var relayIP: String = ""
    var responseTime: Long = INIT_TIME_MS
    var response: String = ""
    var status: String = ""
    var dnsType: Int = 0
    var responseIps: String = ""
    var resolverId: String = ""

    override fun equals(other: Any?): Boolean {
        if (other !is DnsLog) return false
        if (id != other.id) return false
        return true
    }

    override fun hashCode(): Int {
        return this.id.hashCode()
    }

    fun wallTime(): String {
        val date = Date(this.time)
        val format = SimpleDateFormat(Constants.DATE_FORMAT_PATTERN, Locale.ROOT)
        return format.format(date)
    }

    fun groundedQuery(): Boolean {
        return (this.status != Transaction.Status.COMPLETE.toString() ||
            this.response == Constants.NXDOMAIN ||
            this.isBlocked)
    }

    fun unansweredQuery(): Boolean {
        return (this.status != Transaction.Status.COMPLETE.toString() ||
            this.response == Constants.NXDOMAIN)
    }

    fun isAnonymized(): Boolean {
        return this.relayIP.isNotEmpty()
    }

    fun isLocallyAnswered(): Boolean {
        return this.serverIP.isEmpty()
    }

    fun hasBlocklists(): Boolean {
        return this.blockLists.isNotEmpty()
    }

    fun getBlocklists(): List<String> {
        return this.blockLists.split(",")
    }
}
