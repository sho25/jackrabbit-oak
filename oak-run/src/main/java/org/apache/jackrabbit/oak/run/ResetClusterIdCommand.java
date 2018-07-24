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
name|CommitFailedException
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
name|Type
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
name|segment
operator|.
name|SegmentNodeStoreBuilders
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
name|cluster
operator|.
name|ClusterRepositoryInfo
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
name|commit
operator|.
name|CommitInfo
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
name|commit
operator|.
name|EmptyHook
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
name|NodeBuilder
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
name|MongoClientURI
import|;
end_import

begin_import
import|import
name|com
operator|.
name|mongodb
operator|.
name|MongoURI
import|;
end_import

begin_import
import|import
name|joptsimple
operator|.
name|OptionParser
import|;
end_import

begin_import
import|import
name|joptsimple
operator|.
name|OptionSet
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
name|plugins
operator|.
name|document
operator|.
name|mongo
operator|.
name|MongoDocumentNodeStoreBuilder
operator|.
name|newMongoDocumentNodeStoreBuilder
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
name|segment
operator|.
name|file
operator|.
name|FileStoreBuilder
operator|.
name|fileStoreBuilder
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

begin_comment
comment|/**  * OFFLINE utility to delete the clusterId stored as hidden  * property as defined by ClusterRepositoryInfo.  *<p>  * This utility is meant to be the only mechanism to delete  * this id and yes, it is meant to be used offline only  * (as otherwise this would correspond to breaking the  * requirement that the clusterId be stable and persistent).  *<p>  * Target use case for this tool is to avoid duplicate   * clusterIds after a repository was cloned.  */
end_comment

begin_class
class|class
name|ResetClusterIdCommand
implements|implements
name|Command
block|{
specifier|private
specifier|static
name|void
name|deleteClusterId
parameter_list|(
name|NodeStore
name|store
parameter_list|)
block|{
name|NodeBuilder
name|builder
init|=
name|store
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|NodeBuilder
name|clusterConfigNode
init|=
name|builder
operator|.
name|getChildNode
argument_list|(
name|ClusterRepositoryInfo
operator|.
name|CLUSTER_CONFIG_NODE
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|clusterConfigNode
operator|.
name|exists
argument_list|()
condition|)
block|{
comment|// if it doesn't exist, then there is no way to delete
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"clusterId was never set or already deleted."
argument_list|)
expr_stmt|;
return|return;
block|}
if|if
condition|(
operator|!
name|clusterConfigNode
operator|.
name|hasProperty
argument_list|(
name|ClusterRepositoryInfo
operator|.
name|CLUSTER_ID_PROP
argument_list|)
condition|)
block|{
comment|// the config node exists, but the clusterId not
comment|// so again, no way to delete
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"clusterId was never set or already deleted."
argument_list|)
expr_stmt|;
return|return;
block|}
name|String
name|oldClusterId
init|=
name|clusterConfigNode
operator|.
name|getProperty
argument_list|(
name|ClusterRepositoryInfo
operator|.
name|CLUSTER_ID_PROP
argument_list|)
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|STRING
argument_list|)
decl_stmt|;
name|clusterConfigNode
operator|.
name|removeProperty
argument_list|(
name|ClusterRepositoryInfo
operator|.
name|CLUSTER_ID_PROP
argument_list|)
expr_stmt|;
try|try
block|{
name|store
operator|.
name|merge
argument_list|(
name|builder
argument_list|,
name|EmptyHook
operator|.
name|INSTANCE
argument_list|,
name|CommitInfo
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"clusterId deleted successfully. (old id was "
operator|+
name|oldClusterId
operator|+
literal|")"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|e
parameter_list|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Failed to delete clusterId due to exception: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
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
name|OptionParser
name|parser
init|=
operator|new
name|OptionParser
argument_list|()
decl_stmt|;
name|OptionSet
name|options
init|=
name|parser
operator|.
name|parse
argument_list|(
name|args
argument_list|)
decl_stmt|;
if|if
condition|(
name|options
operator|.
name|nonOptionArguments
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"usage: resetclusterid {<path>|<mongo-uri>}"
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
name|String
name|source
init|=
name|options
operator|.
name|nonOptionArguments
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|toString
argument_list|()
decl_stmt|;
name|Closer
name|closer
init|=
name|Closer
operator|.
name|create
argument_list|()
decl_stmt|;
try|try
block|{
name|NodeStore
name|store
decl_stmt|;
if|if
condition|(
name|args
index|[
literal|0
index|]
operator|.
name|startsWith
argument_list|(
name|MongoURI
operator|.
name|MONGODB_PREFIX
argument_list|)
condition|)
block|{
name|MongoClientURI
name|uri
init|=
operator|new
name|MongoClientURI
argument_list|(
name|source
argument_list|)
decl_stmt|;
name|MongoClient
name|client
init|=
operator|new
name|MongoClient
argument_list|(
name|uri
argument_list|)
decl_stmt|;
specifier|final
name|DocumentNodeStore
name|dns
init|=
name|newMongoDocumentNodeStoreBuilder
argument_list|()
operator|.
name|setMongoDB
argument_list|(
name|client
argument_list|,
name|uri
operator|.
name|getDatabase
argument_list|()
argument_list|)
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
name|store
operator|=
name|dns
expr_stmt|;
block|}
else|else
block|{
name|FileStore
name|fileStore
init|=
name|fileStoreBuilder
argument_list|(
operator|new
name|File
argument_list|(
name|source
argument_list|)
argument_list|)
operator|.
name|withStrictVersionCheck
argument_list|(
literal|true
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|closer
operator|.
name|register
argument_list|(
name|fileStore
argument_list|)
expr_stmt|;
name|store
operator|=
name|SegmentNodeStoreBuilders
operator|.
name|builder
argument_list|(
name|fileStore
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
block|}
name|deleteClusterId
argument_list|(
name|store
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

