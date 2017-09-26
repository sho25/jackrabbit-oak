begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|plugins
operator|.
name|document
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PrintStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|UnknownHostException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|CountDownLatch
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicBoolean
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|BiMap
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|HashBiMap
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Maps
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|io
operator|.
name|ByteStreams
import|;
end_import

begin_import
import|import
name|com
operator|.
name|mongodb
operator|.
name|BasicDBObject
import|;
end_import

begin_import
import|import
name|com
operator|.
name|mongodb
operator|.
name|DB
import|;
end_import

begin_import
import|import
name|com
operator|.
name|mongodb
operator|.
name|DBAddress
import|;
end_import

begin_import
import|import
name|com
operator|.
name|mongodb
operator|.
name|DBCollection
import|;
end_import

begin_import
import|import
name|com
operator|.
name|mongodb
operator|.
name|DBObject
import|;
end_import

begin_import
import|import
name|com
operator|.
name|mongodb
operator|.
name|Mongo
import|;
end_import

begin_import
import|import
name|com
operator|.
name|mongodb
operator|.
name|MongoClient
import|;
end_import

begin_import
import|import
name|com
operator|.
name|mongodb
operator|.
name|QueryBuilder
import|;
end_import

begin_import
import|import
name|com
operator|.
name|mongodb
operator|.
name|WriteConcern
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Ignore
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|commons
operator|.
name|IOUtils
operator|.
name|humanReadableByteCount
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertNotNull
import|;
end_import

begin_class
specifier|public
class|class
name|BlobThroughPutTest
block|{
specifier|private
specifier|static
specifier|final
name|int
name|NO_OF_NODES
init|=
literal|100
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|BLOB_SIZE
init|=
literal|1024
operator|*
literal|1024
operator|*
literal|2
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|TEST_DB1
init|=
literal|"tptest1"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|TEST_DB2
init|=
literal|"tptest2"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|MAX_EXEC_TIME
init|=
literal|5
decl_stmt|;
comment|//In seconds
specifier|private
specifier|static
specifier|final
name|int
index|[]
name|READERS
init|=
block|{
literal|5
block|,
literal|10
block|,
literal|15
block|,
literal|20
block|}
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
index|[]
name|WRITERS
init|=
block|{
literal|0
block|,
literal|1
block|,
literal|2
block|,
literal|4
block|}
decl_stmt|;
specifier|private
specifier|final
name|List
argument_list|<
name|Result
argument_list|>
name|results
init|=
operator|new
name|ArrayList
argument_list|<
name|Result
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|BiMap
argument_list|<
name|WriteConcern
argument_list|,
name|String
argument_list|>
name|namedConcerns
decl_stmt|;
static|static
block|{
name|BiMap
argument_list|<
name|WriteConcern
argument_list|,
name|String
argument_list|>
name|bimap
init|=
name|HashBiMap
operator|.
name|create
argument_list|()
decl_stmt|;
name|bimap
operator|.
name|put
argument_list|(
name|WriteConcern
operator|.
name|FSYNC_SAFE
argument_list|,
literal|"FSYNC_SAFE"
argument_list|)
expr_stmt|;
name|bimap
operator|.
name|put
argument_list|(
name|WriteConcern
operator|.
name|JOURNAL_SAFE
argument_list|,
literal|"JOURNAL_SAFE"
argument_list|)
expr_stmt|;
comment|//        bimap.put(WriteConcern.MAJORITY,"MAJORITY");
name|bimap
operator|.
name|put
argument_list|(
name|WriteConcern
operator|.
name|UNACKNOWLEDGED
argument_list|,
literal|"UNACKNOWLEDGED"
argument_list|)
expr_stmt|;
name|bimap
operator|.
name|put
argument_list|(
name|WriteConcern
operator|.
name|NORMAL
argument_list|,
literal|"NORMAL"
argument_list|)
expr_stmt|;
comment|//        bimap.put(WriteConcern.REPLICAS_SAFE,"REPLICAS_SAFE");
name|bimap
operator|.
name|put
argument_list|(
name|WriteConcern
operator|.
name|SAFE
argument_list|,
literal|"SAFE"
argument_list|)
expr_stmt|;
name|namedConcerns
operator|=
name|Maps
operator|.
name|unmodifiableBiMap
argument_list|(
name|bimap
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|final
name|String
name|localServer
init|=
literal|"localhost:27017/test"
decl_stmt|;
specifier|private
specifier|final
name|String
name|remoteServer
init|=
literal|"remote:27017/test"
decl_stmt|;
annotation|@
name|Ignore
annotation|@
name|Test
specifier|public
name|void
name|performBenchMark
parameter_list|()
throws|throws
name|UnknownHostException
throws|,
name|InterruptedException
block|{
name|MongoClient
name|local
init|=
operator|new
name|MongoClient
argument_list|(
operator|new
name|DBAddress
argument_list|(
name|localServer
argument_list|)
argument_list|)
decl_stmt|;
name|MongoClient
name|remote
init|=
operator|new
name|MongoClient
argument_list|(
operator|new
name|DBAddress
argument_list|(
name|remoteServer
argument_list|)
argument_list|)
decl_stmt|;
name|run
argument_list|(
name|local
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|run
argument_list|(
name|local
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|run
argument_list|(
name|remote
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|run
argument_list|(
name|remote
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|dumpResult
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Ignore
annotation|@
name|Test
specifier|public
name|void
name|performBenchMark_WriteConcern
parameter_list|()
throws|throws
name|UnknownHostException
throws|,
name|InterruptedException
block|{
name|Mongo
name|mongo
init|=
operator|new
name|Mongo
argument_list|(
operator|new
name|DBAddress
argument_list|(
name|remoteServer
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|DB
name|db
init|=
name|mongo
operator|.
name|getDB
argument_list|(
name|TEST_DB1
argument_list|)
decl_stmt|;
specifier|final
name|DBCollection
name|nodes
init|=
name|db
operator|.
name|getCollection
argument_list|(
literal|"nodes"
argument_list|)
decl_stmt|;
specifier|final
name|DBCollection
name|blobs
init|=
name|db
operator|.
name|getCollection
argument_list|(
literal|"blobs"
argument_list|)
decl_stmt|;
name|int
name|readers
init|=
literal|0
decl_stmt|;
name|int
name|writers
init|=
literal|2
decl_stmt|;
for|for
control|(
name|WriteConcern
name|wc
range|:
name|namedConcerns
operator|.
name|keySet
argument_list|()
control|)
block|{
name|prepareDB
argument_list|(
name|nodes
argument_list|,
name|blobs
argument_list|)
expr_stmt|;
specifier|final
name|Benchmark
name|b
init|=
operator|new
name|Benchmark
argument_list|(
name|nodes
argument_list|,
name|blobs
argument_list|)
decl_stmt|;
name|Result
name|r
init|=
name|b
operator|.
name|run
argument_list|(
name|readers
argument_list|,
name|writers
argument_list|,
literal|true
argument_list|,
name|wc
argument_list|)
decl_stmt|;
name|results
operator|.
name|add
argument_list|(
name|r
argument_list|)
expr_stmt|;
block|}
name|prepareDB
argument_list|(
name|nodes
argument_list|,
name|blobs
argument_list|)
expr_stmt|;
name|dumpResult
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|dumpResult
parameter_list|()
block|{
name|PrintStream
name|ps
init|=
name|System
operator|.
name|out
decl_stmt|;
name|ps
operator|.
name|println
argument_list|(
name|Result
operator|.
name|OUTPUT_FORMAT
argument_list|)
expr_stmt|;
for|for
control|(
name|Result
name|r
range|:
name|results
control|)
block|{
name|ps
operator|.
name|println
argument_list|(
name|r
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|run
parameter_list|(
name|Mongo
name|mongo
parameter_list|,
name|boolean
name|useSameDB
parameter_list|,
name|boolean
name|remote
parameter_list|)
throws|throws
name|InterruptedException
block|{
specifier|final
name|DB
name|nodeDB
init|=
name|mongo
operator|.
name|getDB
argument_list|(
name|TEST_DB1
argument_list|)
decl_stmt|;
specifier|final
name|DB
name|blobDB
init|=
name|useSameDB
condition|?
name|mongo
operator|.
name|getDB
argument_list|(
name|TEST_DB1
argument_list|)
else|:
name|mongo
operator|.
name|getDB
argument_list|(
name|TEST_DB2
argument_list|)
decl_stmt|;
specifier|final
name|DBCollection
name|nodes
init|=
name|nodeDB
operator|.
name|getCollection
argument_list|(
literal|"nodes"
argument_list|)
decl_stmt|;
specifier|final
name|DBCollection
name|blobs
init|=
name|blobDB
operator|.
name|getCollection
argument_list|(
literal|"blobs"
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|readers
range|:
name|READERS
control|)
block|{
for|for
control|(
name|int
name|writers
range|:
name|WRITERS
control|)
block|{
name|prepareDB
argument_list|(
name|nodes
argument_list|,
name|blobs
argument_list|)
expr_stmt|;
specifier|final
name|Benchmark
name|b
init|=
operator|new
name|Benchmark
argument_list|(
name|nodes
argument_list|,
name|blobs
argument_list|)
decl_stmt|;
name|Result
name|r
init|=
name|b
operator|.
name|run
argument_list|(
name|readers
argument_list|,
name|writers
argument_list|,
name|remote
argument_list|,
name|WriteConcern
operator|.
name|SAFE
argument_list|)
decl_stmt|;
name|results
operator|.
name|add
argument_list|(
name|r
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|private
name|void
name|prepareDB
parameter_list|(
name|DBCollection
name|nodes
parameter_list|,
name|DBCollection
name|blobs
parameter_list|)
block|{
name|MongoUtils
operator|.
name|dropCollections
argument_list|(
name|nodes
operator|.
name|getDB
argument_list|()
argument_list|)
expr_stmt|;
name|MongoUtils
operator|.
name|dropCollections
argument_list|(
name|blobs
operator|.
name|getDB
argument_list|()
argument_list|)
expr_stmt|;
name|createTestNodes
argument_list|(
name|nodes
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|createTestNodes
parameter_list|(
name|DBCollection
name|nodes
parameter_list|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|NO_OF_NODES
condition|;
name|i
operator|++
control|)
block|{
name|DBObject
name|obj
init|=
operator|new
name|BasicDBObject
argument_list|(
literal|"_id"
argument_list|,
name|i
argument_list|)
operator|.
name|append
argument_list|(
literal|"foo"
argument_list|,
literal|"bar1"
operator|+
name|i
argument_list|)
decl_stmt|;
name|nodes
operator|.
name|insert
argument_list|(
name|obj
argument_list|,
name|WriteConcern
operator|.
name|SAFE
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
class|class
name|Result
block|{
specifier|final
specifier|static
name|String
name|OUTPUT_FORMAT
init|=
literal|"remote, samedb, readers, writers, reads, writes, "
operator|+
literal|"time, readThroughPut, writeThroughPut, writeConcern"
decl_stmt|;
name|int
name|totalReads
decl_stmt|;
name|int
name|totalWrites
init|=
literal|0
decl_stmt|;
name|int
name|noOfReaders
decl_stmt|;
name|int
name|noOfWriters
decl_stmt|;
name|int
name|execTime
decl_stmt|;
name|int
name|dataSize
init|=
name|BLOB_SIZE
decl_stmt|;
name|boolean
name|sameDB
decl_stmt|;
name|boolean
name|remote
decl_stmt|;
name|WriteConcern
name|writeConcern
decl_stmt|;
name|double
name|readThroughPut
parameter_list|()
block|{
return|return
name|totalReads
operator|/
name|execTime
return|;
block|}
name|double
name|writeThroughPut
parameter_list|()
block|{
return|return
name|totalWrites
operator|*
name|dataSize
operator|/
name|execTime
return|;
block|}
name|String
name|getWriteConcern
parameter_list|()
block|{
return|return
name|namedConcerns
operator|.
name|get
argument_list|(
name|writeConcern
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|String
operator|.
name|format
argument_list|(
literal|"%s,%s,%d,%d,%d,%d,%d,%1.0f,%s,%s"
argument_list|,
name|remote
argument_list|,
name|sameDB
argument_list|,
name|noOfReaders
argument_list|,
name|noOfWriters
argument_list|,
name|totalReads
argument_list|,
name|totalWrites
argument_list|,
name|execTime
argument_list|,
name|readThroughPut
argument_list|()
argument_list|,
name|humanReadableByteCount
argument_list|(
operator|(
name|long
operator|)
name|writeThroughPut
argument_list|()
argument_list|)
argument_list|,
name|getWriteConcern
argument_list|()
argument_list|)
return|;
block|}
block|}
specifier|private
specifier|static
class|class
name|Benchmark
block|{
specifier|private
specifier|final
name|DBCollection
name|nodes
decl_stmt|;
specifier|private
specifier|final
name|DBCollection
name|blobs
decl_stmt|;
specifier|private
specifier|final
name|Random
name|random
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|AtomicBoolean
name|stopTest
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|false
argument_list|)
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|byte
index|[]
name|DATA
decl_stmt|;
specifier|private
specifier|final
name|CountDownLatch
name|startLatch
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
static|static
block|{
try|try
block|{
name|DATA
operator|=
name|ByteStreams
operator|.
name|toByteArray
argument_list|(
operator|new
name|RandomStream
argument_list|(
name|BLOB_SIZE
argument_list|,
literal|100
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
specifier|private
name|Benchmark
parameter_list|(
name|DBCollection
name|nodes
parameter_list|,
name|DBCollection
name|blobs
parameter_list|)
block|{
name|this
operator|.
name|nodes
operator|=
name|nodes
expr_stmt|;
name|this
operator|.
name|blobs
operator|=
name|blobs
expr_stmt|;
block|}
specifier|public
name|Result
name|run
parameter_list|(
name|int
name|noOfReaders
parameter_list|,
name|int
name|noOfWriters
parameter_list|,
name|boolean
name|remote
parameter_list|,
name|WriteConcern
name|writeConcern
parameter_list|)
throws|throws
name|InterruptedException
block|{
name|boolean
name|sameDB
init|=
name|nodes
operator|.
name|getDB
argument_list|()
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|blobs
operator|.
name|getDB
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Reader
argument_list|>
name|readers
init|=
operator|new
name|ArrayList
argument_list|<
name|Reader
argument_list|>
argument_list|(
name|noOfReaders
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Writer
argument_list|>
name|writers
init|=
operator|new
name|ArrayList
argument_list|<
name|Writer
argument_list|>
argument_list|(
name|noOfWriters
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Runnable
argument_list|>
name|runnables
init|=
operator|new
name|ArrayList
argument_list|<
name|Runnable
argument_list|>
argument_list|(
name|noOfReaders
operator|+
name|noOfWriters
argument_list|)
decl_stmt|;
specifier|final
name|CountDownLatch
name|stopLatch
init|=
operator|new
name|CountDownLatch
argument_list|(
name|noOfReaders
operator|+
name|noOfWriters
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|noOfReaders
condition|;
name|i
operator|++
control|)
block|{
name|readers
operator|.
name|add
argument_list|(
operator|new
name|Reader
argument_list|(
name|stopLatch
argument_list|)
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|noOfWriters
condition|;
name|i
operator|++
control|)
block|{
name|writers
operator|.
name|add
argument_list|(
operator|new
name|Writer
argument_list|(
name|i
argument_list|,
name|stopLatch
argument_list|,
name|writeConcern
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|runnables
operator|.
name|addAll
argument_list|(
name|readers
argument_list|)
expr_stmt|;
name|runnables
operator|.
name|addAll
argument_list|(
name|writers
argument_list|)
expr_stmt|;
name|stopTest
operator|.
name|set
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Thread
argument_list|>
name|threads
init|=
operator|new
name|ArrayList
argument_list|<
name|Thread
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|runnables
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Thread
name|worker
init|=
operator|new
name|Thread
argument_list|(
name|runnables
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
decl_stmt|;
name|worker
operator|.
name|start
argument_list|()
expr_stmt|;
name|threads
operator|.
name|add
argument_list|(
name|worker
argument_list|)
expr_stmt|;
block|}
name|System
operator|.
name|err
operator|.
name|printf
argument_list|(
literal|"Running with [%d] readers and [%d] writers. "
operator|+
literal|"Same DB [%s], Remote server [%s], Max Time [%d] seconds, WriteConcern [%s] %n"
argument_list|,
name|noOfReaders
argument_list|,
name|noOfWriters
argument_list|,
name|sameDB
argument_list|,
name|remote
argument_list|,
name|MAX_EXEC_TIME
argument_list|,
name|namedConcerns
operator|.
name|get
argument_list|(
name|writeConcern
argument_list|)
argument_list|)
expr_stmt|;
name|startLatch
operator|.
name|countDown
argument_list|()
expr_stmt|;
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|sleep
argument_list|(
name|MAX_EXEC_TIME
argument_list|)
expr_stmt|;
name|stopTest
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|stopLatch
operator|.
name|await
argument_list|()
expr_stmt|;
name|int
name|totalReads
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Reader
name|r
range|:
name|readers
control|)
block|{
name|totalReads
operator|+=
name|r
operator|.
name|readCount
expr_stmt|;
block|}
name|int
name|totalWrites
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Writer
name|w
range|:
name|writers
control|)
block|{
name|totalWrites
operator|+=
name|w
operator|.
name|writeCount
expr_stmt|;
block|}
name|Result
name|r
init|=
operator|new
name|Result
argument_list|()
decl_stmt|;
name|r
operator|.
name|execTime
operator|=
name|MAX_EXEC_TIME
expr_stmt|;
name|r
operator|.
name|noOfReaders
operator|=
name|noOfReaders
expr_stmt|;
name|r
operator|.
name|noOfWriters
operator|=
name|noOfWriters
expr_stmt|;
name|r
operator|.
name|totalReads
operator|=
name|totalReads
expr_stmt|;
name|r
operator|.
name|totalWrites
operator|=
name|totalWrites
expr_stmt|;
name|r
operator|.
name|remote
operator|=
name|remote
expr_stmt|;
name|r
operator|.
name|sameDB
operator|=
name|sameDB
expr_stmt|;
name|r
operator|.
name|writeConcern
operator|=
name|writeConcern
expr_stmt|;
name|System
operator|.
name|err
operator|.
name|printf
argument_list|(
literal|"Run complete. Reads [%d] and writes [%d] %n"
argument_list|,
name|totalReads
argument_list|,
name|totalWrites
argument_list|)
expr_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
name|r
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|r
return|;
block|}
specifier|private
name|void
name|waitForStart
parameter_list|()
block|{
try|try
block|{
name|startLatch
operator|.
name|await
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
specifier|private
class|class
name|Reader
implements|implements
name|Runnable
block|{
name|int
name|readCount
init|=
literal|0
decl_stmt|;
specifier|final
name|CountDownLatch
name|stopLatch
decl_stmt|;
specifier|public
name|Reader
parameter_list|(
name|CountDownLatch
name|stopLatch
parameter_list|)
block|{
name|this
operator|.
name|stopLatch
operator|=
name|stopLatch
expr_stmt|;
block|}
specifier|public
name|void
name|run
parameter_list|()
block|{
name|waitForStart
argument_list|()
expr_stmt|;
while|while
condition|(
operator|!
name|stopTest
operator|.
name|get
argument_list|()
condition|)
block|{
name|int
name|id
init|=
name|random
operator|.
name|nextInt
argument_list|(
name|NO_OF_NODES
argument_list|)
decl_stmt|;
name|DBObject
name|o
init|=
name|nodes
operator|.
name|findOne
argument_list|(
name|QueryBuilder
operator|.
name|start
argument_list|(
literal|"_id"
argument_list|)
operator|.
name|is
argument_list|(
name|id
argument_list|)
operator|.
name|get
argument_list|()
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"did not found object with id "
operator|+
name|id
argument_list|,
name|o
argument_list|)
expr_stmt|;
name|readCount
operator|++
expr_stmt|;
block|}
name|stopLatch
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
class|class
name|Writer
implements|implements
name|Runnable
block|{
name|int
name|writeCount
init|=
literal|0
decl_stmt|;
specifier|final
name|int
name|id
decl_stmt|;
specifier|final
name|CountDownLatch
name|stopLatch
decl_stmt|;
specifier|final
name|WriteConcern
name|writeConcern
decl_stmt|;
specifier|private
name|Writer
parameter_list|(
name|int
name|id
parameter_list|,
name|CountDownLatch
name|stopLatch
parameter_list|,
name|WriteConcern
name|writeConcern
parameter_list|)
block|{
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
name|this
operator|.
name|stopLatch
operator|=
name|stopLatch
expr_stmt|;
name|this
operator|.
name|writeConcern
operator|=
name|writeConcern
expr_stmt|;
block|}
specifier|public
name|void
name|run
parameter_list|()
block|{
name|waitForStart
argument_list|()
expr_stmt|;
while|while
condition|(
operator|!
name|stopTest
operator|.
name|get
argument_list|()
condition|)
block|{
name|String
name|_id
init|=
name|id
operator|+
literal|"-"
operator|+
name|writeCount
decl_stmt|;
name|DBObject
name|obj
init|=
operator|new
name|BasicDBObject
argument_list|()
operator|.
name|append
argument_list|(
literal|"foo"
argument_list|,
name|_id
argument_list|)
decl_stmt|;
name|obj
operator|.
name|put
argument_list|(
literal|"blob"
argument_list|,
name|DATA
argument_list|)
expr_stmt|;
name|blobs
operator|.
name|insert
argument_list|(
name|obj
argument_list|,
name|writeConcern
argument_list|)
expr_stmt|;
name|writeCount
operator|++
expr_stmt|;
block|}
name|stopLatch
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

