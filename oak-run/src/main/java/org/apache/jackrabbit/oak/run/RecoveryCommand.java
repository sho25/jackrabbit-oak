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
import|import
name|java
operator|.
name|io
operator|.
name|Closeable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
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
operator|.
name|ClusterNodeInfoDocument
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
name|document
operator|.
name|DocumentNodeStore
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
name|document
operator|.
name|DocumentNodeStoreBuilder
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
name|document
operator|.
name|DocumentStore
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
name|document
operator|.
name|LastRevRecoveryAgent
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
name|document
operator|.
name|MissingLastRevSeeker
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
name|document
operator|.
name|NodeDocument
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
name|document
operator|.
name|mongo
operator|.
name|MongoDocumentStore
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
name|document
operator|.
name|mongo
operator|.
name|MongoMissingLastRevSeeker
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
name|document
operator|.
name|rdb
operator|.
name|RDBDocumentStore
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
name|document
operator|.
name|rdb
operator|.
name|RDBMissingLastRevSeeker
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
name|document
operator|.
name|util
operator|.
name|MapDBMapFactory
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
name|document
operator|.
name|util
operator|.
name|MapFactory
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
name|run
operator|.
name|commons
operator|.
name|Command
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
name|blob
operator|.
name|MemoryBlobStore
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
name|Closer
import|;
end_import

begin_class
class|class
name|RecoveryCommand
implements|implements
name|Command
block|{
annotation|@
name|Override
specifier|public
name|void
name|execute
parameter_list|(
name|String
modifier|...
name|args
parameter_list|)
throws|throws
name|Exception
block|{
name|MapFactory
operator|.
name|setInstance
argument_list|(
operator|new
name|MapDBMapFactory
argument_list|()
argument_list|)
expr_stmt|;
name|Closer
name|closer
init|=
name|Utils
operator|.
name|createCloserWithShutdownHook
argument_list|()
decl_stmt|;
name|String
name|h
init|=
literal|"recovery mongodb://host:port/database|jdbc:... { dryRun }"
decl_stmt|;
try|try
block|{
name|DocumentNodeStoreBuilder
argument_list|<
name|?
argument_list|>
name|builder
init|=
name|Utils
operator|.
name|createDocumentMKBuilder
argument_list|(
name|args
argument_list|,
name|closer
argument_list|,
name|h
argument_list|)
decl_stmt|;
if|if
condition|(
name|builder
operator|==
literal|null
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Recovery only available for DocumentNodeStore backed by MongoDB or RDB persistence"
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
comment|// The recovery command does not have options for the blob store
comment|// and the DocumentNodeStoreBuilder by default assumes the blobs
comment|// are stored in the same location as the documents. That is,
comment|// either in MongoDB or RDB, which is not necessarily the case and
comment|// can cause an exception when the blob store implementation starts
comment|// read-only on a database that does not have the required
comment|// collection. Use an in-memory blob store instead, because the
comment|// recovery command does not read blobs anyway.
name|builder
operator|.
name|setBlobStore
argument_list|(
operator|new
name|MemoryBlobStore
argument_list|()
argument_list|)
expr_stmt|;
comment|// dryRun implies readonly repo
name|boolean
name|dryRun
init|=
name|Arrays
operator|.
name|asList
argument_list|(
name|args
argument_list|)
operator|.
name|contains
argument_list|(
literal|"dryRun"
argument_list|)
decl_stmt|;
if|if
condition|(
name|dryRun
condition|)
block|{
name|builder
operator|.
name|setReadOnlyMode
argument_list|()
expr_stmt|;
block|}
name|DocumentNodeStore
name|dns
init|=
name|builder
operator|.
name|build
argument_list|()
decl_stmt|;
name|closer
operator|.
name|register
argument_list|(
name|Utils
operator|.
name|asCloseable
argument_list|(
name|dns
argument_list|)
argument_list|)
expr_stmt|;
name|DocumentStore
name|ds
init|=
name|builder
operator|.
name|getDocumentStore
argument_list|()
decl_stmt|;
name|LastRevRecoveryAgent
name|agent
init|=
literal|null
decl_stmt|;
name|MissingLastRevSeeker
name|seeker
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|ds
operator|instanceof
name|MongoDocumentStore
condition|)
block|{
name|MongoDocumentStore
name|docStore
init|=
operator|(
name|MongoDocumentStore
operator|)
name|ds
decl_stmt|;
name|agent
operator|=
operator|new
name|LastRevRecoveryAgent
argument_list|(
name|docStore
argument_list|,
name|dns
argument_list|)
expr_stmt|;
name|seeker
operator|=
operator|new
name|MongoMissingLastRevSeeker
argument_list|(
name|docStore
argument_list|,
name|dns
operator|.
name|getClock
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|ds
operator|instanceof
name|RDBDocumentStore
condition|)
block|{
name|RDBDocumentStore
name|docStore
init|=
operator|(
name|RDBDocumentStore
operator|)
name|ds
decl_stmt|;
name|agent
operator|=
operator|new
name|LastRevRecoveryAgent
argument_list|(
name|docStore
argument_list|,
name|dns
argument_list|)
expr_stmt|;
name|seeker
operator|=
operator|new
name|RDBMissingLastRevSeeker
argument_list|(
name|docStore
argument_list|,
name|dns
operator|.
name|getClock
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|agent
operator|==
literal|null
operator|||
name|seeker
operator|==
literal|null
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Recovery only available for MongoDocumentStore and RDBDocumentStore (this: "
operator|+
name|ds
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|")"
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
if|if
condition|(
name|builder
operator|.
name|getClusterId
argument_list|()
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
literal|"Please specify the clusterId for recovery using --clusterId"
argument_list|)
expr_stmt|;
try|try
block|{
name|List
argument_list|<
name|ClusterNodeInfoDocument
argument_list|>
name|all
init|=
name|ClusterNodeInfoDocument
operator|.
name|all
argument_list|(
name|ds
argument_list|)
decl_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Existing entries in the clusternodes collection are:"
argument_list|)
expr_stmt|;
for|for
control|(
name|ClusterNodeInfoDocument
name|c
range|:
name|all
control|)
block|{
name|String
name|state
init|=
name|c
operator|.
name|isActive
argument_list|()
condition|?
literal|"ACTIVE"
else|:
literal|"INACTIVE"
decl_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
name|c
operator|.
name|getClusterId
argument_list|()
operator|+
literal|" ("
operator|+
name|state
operator|+
literal|"): "
operator|+
name|c
operator|.
name|toString
argument_list|()
operator|.
name|replace
argument_list|(
literal|'\n'
argument_list|,
literal|' '
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|(
name|System
operator|.
name|err
argument_list|)
expr_stmt|;
block|}
name|System
operator|.
name|exit
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
name|Iterable
argument_list|<
name|NodeDocument
argument_list|>
name|docs
init|=
name|seeker
operator|.
name|getCandidates
argument_list|(
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
name|docs
operator|instanceof
name|Closeable
condition|)
block|{
name|closer
operator|.
name|register
argument_list|(
operator|(
name|Closeable
operator|)
name|docs
argument_list|)
expr_stmt|;
block|}
name|agent
operator|.
name|recover
argument_list|(
name|docs
argument_list|,
name|builder
operator|.
name|getClusterId
argument_list|()
argument_list|,
name|dryRun
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
throw|throw
name|closer
operator|.
name|rethrow
argument_list|(
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
name|closer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

