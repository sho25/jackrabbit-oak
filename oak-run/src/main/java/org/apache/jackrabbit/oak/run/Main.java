begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|run
package|;
end_package

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Sets
operator|.
name|newHashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Properties
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Queue
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|UUID
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|Repository
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|core
operator|.
name|RepositoryContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|core
operator|.
name|config
operator|.
name|RepositoryConfig
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|mk
operator|.
name|api
operator|.
name|MicroKernel
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|mk
operator|.
name|core
operator|.
name|MicroKernelImpl
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|Oak
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|api
operator|.
name|ContentRepository
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|benchmark
operator|.
name|BenchmarkRunner
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|http
operator|.
name|OakServlet
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|jcr
operator|.
name|Jcr
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|kernel
operator|.
name|KernelNodeStore
import|;
end_import

begin_import
import|import
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
name|segment
operator|.
name|Segment
import|;
end_import

begin_import
import|import
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
name|segment
operator|.
name|SegmentIdFactory
import|;
end_import

begin_import
import|import
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
name|segment
operator|.
name|SegmentNodeStore
import|;
end_import

begin_import
import|import
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
name|segment
operator|.
name|file
operator|.
name|FileStore
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|spi
operator|.
name|state
operator|.
name|NodeStore
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|upgrade
operator|.
name|RepositoryUpgrade
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|webdav
operator|.
name|jcr
operator|.
name|JCRWebdavServerServlet
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|webdav
operator|.
name|server
operator|.
name|AbstractWebdavServlet
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|webdav
operator|.
name|simple
operator|.
name|SimpleWebdavServlet
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eclipse
operator|.
name|jetty
operator|.
name|server
operator|.
name|Server
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eclipse
operator|.
name|jetty
operator|.
name|servlet
operator|.
name|ServletContextHandler
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eclipse
operator|.
name|jetty
operator|.
name|servlet
operator|.
name|ServletHolder
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
name|collect
operator|.
name|Queues
import|;
end_import

begin_class
specifier|public
class|class
name|Main
block|{
specifier|public
specifier|static
specifier|final
name|int
name|PORT
init|=
literal|8080
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|URI
init|=
literal|"http://localhost:"
operator|+
name|PORT
operator|+
literal|"/"
decl_stmt|;
specifier|private
name|Main
parameter_list|()
block|{     }
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|Exception
block|{
name|printProductInfo
argument_list|()
expr_stmt|;
name|String
name|command
init|=
literal|"server"
decl_stmt|;
if|if
condition|(
name|args
operator|.
name|length
operator|>
literal|0
condition|)
block|{
name|command
operator|=
name|args
index|[
literal|0
index|]
expr_stmt|;
name|String
index|[]
name|tail
init|=
operator|new
name|String
index|[
name|args
operator|.
name|length
operator|-
literal|1
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|args
argument_list|,
literal|1
argument_list|,
name|tail
argument_list|,
literal|0
argument_list|,
name|tail
operator|.
name|length
argument_list|)
expr_stmt|;
name|args
operator|=
name|tail
expr_stmt|;
block|}
if|if
condition|(
literal|"mk"
operator|.
name|equals
argument_list|(
name|command
argument_list|)
condition|)
block|{
name|MicroKernelServer
operator|.
name|main
argument_list|(
name|args
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"benchmark"
operator|.
name|equals
argument_list|(
name|command
argument_list|)
condition|)
block|{
name|BenchmarkRunner
operator|.
name|main
argument_list|(
name|args
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"server"
operator|.
name|equals
argument_list|(
name|command
argument_list|)
condition|)
block|{
operator|new
name|HttpServer
argument_list|(
name|URI
argument_list|,
name|args
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"upgrade"
operator|.
name|equals
argument_list|(
name|command
argument_list|)
condition|)
block|{
if|if
condition|(
name|args
operator|.
name|length
operator|==
literal|2
condition|)
block|{
name|upgrade
argument_list|(
name|args
index|[
literal|0
index|]
argument_list|,
name|args
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"usage: upgrade<olddir><newdir>"
argument_list|)
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
literal|"tarmk"
operator|.
name|equals
argument_list|(
name|command
argument_list|)
condition|)
block|{
if|if
condition|(
name|args
operator|.
name|length
operator|==
literal|0
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"usage: tarmk<path> [id...]"
argument_list|)
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"TarMK "
operator|+
name|args
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|File
name|file
init|=
operator|new
name|File
argument_list|(
name|args
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
name|FileStore
name|store
init|=
operator|new
name|FileStore
argument_list|(
name|file
argument_list|,
literal|256
argument_list|,
literal|false
argument_list|)
decl_stmt|;
try|try
block|{
if|if
condition|(
name|args
operator|.
name|length
operator|==
literal|1
condition|)
block|{
name|Map
argument_list|<
name|UUID
argument_list|,
name|List
argument_list|<
name|UUID
argument_list|>
argument_list|>
name|idmap
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
name|int
name|dataCount
init|=
literal|0
decl_stmt|;
name|long
name|dataSize
init|=
literal|0
decl_stmt|;
name|int
name|bulkCount
init|=
literal|0
decl_stmt|;
name|long
name|bulkSize
init|=
literal|0
decl_stmt|;
for|for
control|(
name|UUID
name|uuid
range|:
name|store
operator|.
name|getSegmentIds
argument_list|()
control|)
block|{
if|if
condition|(
name|SegmentIdFactory
operator|.
name|isDataSegmentId
argument_list|(
name|uuid
argument_list|)
condition|)
block|{
name|Segment
name|segment
init|=
name|store
operator|.
name|readSegment
argument_list|(
name|uuid
argument_list|)
decl_stmt|;
name|dataCount
operator|++
expr_stmt|;
name|dataSize
operator|+=
name|segment
operator|.
name|size
argument_list|()
expr_stmt|;
name|idmap
operator|.
name|put
argument_list|(
name|uuid
argument_list|,
name|segment
operator|.
name|getReferencedIds
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|SegmentIdFactory
operator|.
name|isBulkSegmentId
argument_list|(
name|uuid
argument_list|)
condition|)
block|{
name|bulkCount
operator|++
expr_stmt|;
name|bulkSize
operator|+=
name|store
operator|.
name|readSegment
argument_list|(
name|uuid
argument_list|)
operator|.
name|size
argument_list|()
expr_stmt|;
name|idmap
operator|.
name|put
argument_list|(
name|uuid
argument_list|,
name|Collections
operator|.
expr|<
name|UUID
operator|>
name|emptyList
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Total size:"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|format
argument_list|(
literal|"%6dMB in %6d data segments%n"
argument_list|,
name|dataSize
operator|/
operator|(
literal|1024
operator|*
literal|1024
operator|)
argument_list|,
name|dataCount
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|format
argument_list|(
literal|"%6dMB in %6d bulk segments%n"
argument_list|,
name|bulkSize
operator|/
operator|(
literal|1024
operator|*
literal|1024
operator|)
argument_list|,
name|bulkCount
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|UUID
argument_list|>
name|garbage
init|=
name|newHashSet
argument_list|(
name|idmap
operator|.
name|keySet
argument_list|()
argument_list|)
decl_stmt|;
name|Queue
argument_list|<
name|UUID
argument_list|>
name|queue
init|=
name|Queues
operator|.
name|newArrayDeque
argument_list|()
decl_stmt|;
name|queue
operator|.
name|add
argument_list|(
name|store
operator|.
name|getJournal
argument_list|(
literal|"root"
argument_list|)
operator|.
name|getHead
argument_list|()
operator|.
name|getSegmentId
argument_list|()
argument_list|)
expr_stmt|;
while|while
condition|(
operator|!
name|queue
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|UUID
name|id
init|=
name|queue
operator|.
name|remove
argument_list|()
decl_stmt|;
if|if
condition|(
name|garbage
operator|.
name|remove
argument_list|(
name|id
argument_list|)
condition|)
block|{
name|queue
operator|.
name|addAll
argument_list|(
name|idmap
operator|.
name|get
argument_list|(
name|id
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|dataCount
operator|=
literal|0
expr_stmt|;
name|dataSize
operator|=
literal|0
expr_stmt|;
name|bulkCount
operator|=
literal|0
expr_stmt|;
name|bulkSize
operator|=
literal|0
expr_stmt|;
for|for
control|(
name|UUID
name|uuid
range|:
name|garbage
control|)
block|{
if|if
condition|(
name|SegmentIdFactory
operator|.
name|isDataSegmentId
argument_list|(
name|uuid
argument_list|)
condition|)
block|{
name|dataCount
operator|++
expr_stmt|;
name|dataSize
operator|+=
name|store
operator|.
name|readSegment
argument_list|(
name|uuid
argument_list|)
operator|.
name|size
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|SegmentIdFactory
operator|.
name|isBulkSegmentId
argument_list|(
name|uuid
argument_list|)
condition|)
block|{
name|bulkCount
operator|++
expr_stmt|;
name|bulkSize
operator|+=
name|store
operator|.
name|readSegment
argument_list|(
name|uuid
argument_list|)
operator|.
name|size
argument_list|()
expr_stmt|;
block|}
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Available for garbage collection:"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|format
argument_list|(
literal|"%6dkB in %6d data segments%n"
argument_list|,
name|dataSize
operator|/
literal|1024
argument_list|,
name|dataCount
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|format
argument_list|(
literal|"%6dMB in %6d bulk segments%n"
argument_list|,
name|bulkSize
operator|/
operator|(
literal|1024
operator|*
literal|1024
operator|)
argument_list|,
name|bulkCount
argument_list|)
expr_stmt|;
block|}
else|else
block|{
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|args
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|UUID
name|uuid
init|=
name|UUID
operator|.
name|fromString
argument_list|(
name|args
index|[
name|i
index|]
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|store
operator|.
name|readSegment
argument_list|(
name|uuid
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
finally|finally
block|{
name|store
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Unknown command: "
operator|+
name|command
argument_list|)
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
name|void
name|upgrade
parameter_list|(
name|String
name|olddir
parameter_list|,
name|String
name|newdir
parameter_list|)
throws|throws
name|Exception
block|{
name|RepositoryContext
name|source
init|=
name|RepositoryContext
operator|.
name|create
argument_list|(
name|RepositoryConfig
operator|.
name|create
argument_list|(
operator|new
name|File
argument_list|(
name|olddir
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
name|FileStore
name|store
init|=
operator|new
name|FileStore
argument_list|(
operator|new
name|File
argument_list|(
name|newdir
argument_list|)
argument_list|,
literal|256
argument_list|,
literal|true
argument_list|)
decl_stmt|;
try|try
block|{
name|NodeStore
name|target
init|=
operator|new
name|SegmentNodeStore
argument_list|(
name|store
argument_list|)
decl_stmt|;
operator|new
name|RepositoryUpgrade
argument_list|(
name|source
argument_list|,
name|target
argument_list|)
operator|.
name|copy
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|store
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|source
operator|.
name|getRepository
argument_list|()
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
name|void
name|printProductInfo
parameter_list|()
block|{
name|String
name|version
init|=
literal|null
decl_stmt|;
try|try
block|{
name|InputStream
name|stream
init|=
name|Main
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
literal|"/META-INF/maven/org.apache.jackrabbit/oak-run/pom.properties"
argument_list|)
decl_stmt|;
if|if
condition|(
name|stream
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|Properties
name|properties
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|properties
operator|.
name|load
argument_list|(
name|stream
argument_list|)
expr_stmt|;
name|version
operator|=
name|properties
operator|.
name|getProperty
argument_list|(
literal|"version"
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|stream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|ignore
parameter_list|)
block|{         }
name|String
name|product
decl_stmt|;
if|if
condition|(
name|version
operator|!=
literal|null
condition|)
block|{
name|product
operator|=
literal|"Apache Jackrabbit Oak "
operator|+
name|version
expr_stmt|;
block|}
else|else
block|{
name|product
operator|=
literal|"Apache Jackrabbit Oak"
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|product
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|static
class|class
name|HttpServer
block|{
specifier|private
specifier|final
name|ServletContextHandler
name|context
decl_stmt|;
specifier|private
specifier|final
name|Server
name|server
decl_stmt|;
specifier|private
specifier|final
name|MicroKernel
index|[]
name|kernels
decl_stmt|;
specifier|public
name|HttpServer
parameter_list|(
name|String
name|uri
parameter_list|,
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|Exception
block|{
name|int
name|port
init|=
name|java
operator|.
name|net
operator|.
name|URI
operator|.
name|create
argument_list|(
name|uri
argument_list|)
operator|.
name|getPort
argument_list|()
decl_stmt|;
if|if
condition|(
name|port
operator|==
operator|-
literal|1
condition|)
block|{
comment|// use default
name|port
operator|=
name|PORT
expr_stmt|;
block|}
name|context
operator|=
operator|new
name|ServletContextHandler
argument_list|()
expr_stmt|;
name|context
operator|.
name|setContextPath
argument_list|(
literal|"/"
argument_list|)
expr_stmt|;
if|if
condition|(
name|args
operator|.
name|length
operator|==
literal|0
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Starting an in-memory repository"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|uri
operator|+
literal|" -> [memory]"
argument_list|)
expr_stmt|;
name|kernels
operator|=
operator|new
name|MicroKernel
index|[]
block|{
operator|new
name|MicroKernelImpl
argument_list|()
block|}
expr_stmt|;
name|addServlets
argument_list|(
operator|new
name|KernelNodeStore
argument_list|(
name|kernels
index|[
literal|0
index|]
argument_list|)
argument_list|,
literal|""
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|args
operator|.
name|length
operator|==
literal|1
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Starting a standalone repository"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|uri
operator|+
literal|" -> "
operator|+
name|args
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|kernels
operator|=
operator|new
name|MicroKernel
index|[]
block|{
operator|new
name|MicroKernelImpl
argument_list|(
name|args
index|[
literal|0
index|]
argument_list|)
block|}
expr_stmt|;
name|addServlets
argument_list|(
operator|new
name|KernelNodeStore
argument_list|(
name|kernels
index|[
literal|0
index|]
argument_list|)
argument_list|,
literal|""
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Starting a clustered repository"
argument_list|)
expr_stmt|;
name|kernels
operator|=
operator|new
name|MicroKernel
index|[
name|args
operator|.
name|length
index|]
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|args
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
comment|// FIXME: Use a clustered MicroKernel implementation
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|uri
operator|+
literal|"/node"
operator|+
name|i
operator|+
literal|"/ -> "
operator|+
name|args
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|kernels
index|[
name|i
index|]
operator|=
operator|new
name|MicroKernelImpl
argument_list|(
name|args
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|addServlets
argument_list|(
operator|new
name|KernelNodeStore
argument_list|(
name|kernels
index|[
name|i
index|]
argument_list|)
argument_list|,
literal|"/node"
operator|+
name|i
argument_list|)
expr_stmt|;
block|}
block|}
name|server
operator|=
operator|new
name|Server
argument_list|(
name|port
argument_list|)
expr_stmt|;
name|server
operator|.
name|setHandler
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|server
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|join
parameter_list|()
throws|throws
name|Exception
block|{
name|server
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|stop
parameter_list|()
throws|throws
name|Exception
block|{
name|server
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|addServlets
parameter_list|(
name|NodeStore
name|store
parameter_list|,
name|String
name|path
parameter_list|)
block|{
name|Oak
name|oak
init|=
operator|new
name|Oak
argument_list|(
name|store
argument_list|)
decl_stmt|;
name|Jcr
name|jcr
init|=
operator|new
name|Jcr
argument_list|(
name|oak
argument_list|)
decl_stmt|;
name|ContentRepository
name|repository
init|=
name|oak
operator|.
name|createContentRepository
argument_list|()
decl_stmt|;
name|ServletHolder
name|holder
init|=
operator|new
name|ServletHolder
argument_list|(
operator|new
name|OakServlet
argument_list|(
name|repository
argument_list|)
argument_list|)
decl_stmt|;
name|context
operator|.
name|addServlet
argument_list|(
name|holder
argument_list|,
name|path
operator|+
literal|"/*"
argument_list|)
expr_stmt|;
specifier|final
name|Repository
name|jcrRepository
init|=
name|jcr
operator|.
name|createRepository
argument_list|()
decl_stmt|;
name|ServletHolder
name|webdav
init|=
operator|new
name|ServletHolder
argument_list|(
operator|new
name|SimpleWebdavServlet
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Repository
name|getRepository
parameter_list|()
block|{
return|return
name|jcrRepository
return|;
block|}
block|}
argument_list|)
decl_stmt|;
name|webdav
operator|.
name|setInitParameter
argument_list|(
name|SimpleWebdavServlet
operator|.
name|INIT_PARAM_RESOURCE_PATH_PREFIX
argument_list|,
name|path
operator|+
literal|"/webdav"
argument_list|)
expr_stmt|;
name|webdav
operator|.
name|setInitParameter
argument_list|(
name|AbstractWebdavServlet
operator|.
name|INIT_PARAM_AUTHENTICATE_HEADER
argument_list|,
literal|"Basic realm=\"Oak\""
argument_list|)
expr_stmt|;
name|context
operator|.
name|addServlet
argument_list|(
name|webdav
argument_list|,
name|path
operator|+
literal|"/webdav/*"
argument_list|)
expr_stmt|;
name|ServletHolder
name|davex
init|=
operator|new
name|ServletHolder
argument_list|(
operator|new
name|JCRWebdavServerServlet
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|Repository
name|getRepository
parameter_list|()
block|{
return|return
name|jcrRepository
return|;
block|}
block|}
argument_list|)
decl_stmt|;
name|davex
operator|.
name|setInitParameter
argument_list|(
name|JCRWebdavServerServlet
operator|.
name|INIT_PARAM_RESOURCE_PATH_PREFIX
argument_list|,
name|path
operator|+
literal|"/davex"
argument_list|)
expr_stmt|;
name|webdav
operator|.
name|setInitParameter
argument_list|(
name|AbstractWebdavServlet
operator|.
name|INIT_PARAM_AUTHENTICATE_HEADER
argument_list|,
literal|"Basic realm=\"Oak\""
argument_list|)
expr_stmt|;
name|context
operator|.
name|addServlet
argument_list|(
name|davex
argument_list|,
name|path
operator|+
literal|"/davex/*"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

