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
name|plugins
operator|.
name|document
operator|.
name|mongo
package|;
end_package

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
name|AbstractMongoConnectionTest
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
name|ClusterNodeInfo
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
name|DocumentMK
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Lists
operator|.
name|newArrayList
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
name|assertEquals
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
name|AcquireRecoveryLockTest
extends|extends
name|AbstractMongoConnectionTest
block|{
comment|// OAK-4131
annotation|@
name|Test
specifier|public
name|void
name|recoveryBy
parameter_list|()
throws|throws
name|Exception
block|{
name|MongoDocumentStore
name|store
init|=
operator|new
name|MongoDocumentStore
argument_list|(
name|mongoConnection
operator|.
name|getDB
argument_list|()
argument_list|,
operator|new
name|DocumentMK
operator|.
name|Builder
argument_list|()
argument_list|)
decl_stmt|;
name|MongoMissingLastRevSeeker
name|seeker
init|=
operator|new
name|MongoMissingLastRevSeeker
argument_list|(
name|store
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|ClusterNodeInfoDocument
argument_list|>
name|infoDocs
init|=
name|newArrayList
argument_list|(
name|seeker
operator|.
name|getAllClusters
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|infoDocs
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|int
name|clusterId
init|=
name|infoDocs
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getClusterId
argument_list|()
decl_stmt|;
name|int
name|otherClusterId
init|=
name|clusterId
operator|+
literal|1
decl_stmt|;
name|seeker
operator|.
name|acquireRecoveryLock
argument_list|(
name|clusterId
argument_list|,
name|otherClusterId
argument_list|)
expr_stmt|;
name|ClusterNodeInfoDocument
name|doc
init|=
name|seeker
operator|.
name|getClusterNodeInfo
argument_list|(
name|clusterId
argument_list|)
decl_stmt|;
name|Object
name|recoveryBy
init|=
name|doc
operator|.
name|get
argument_list|(
name|ClusterNodeInfo
operator|.
name|REV_RECOVERY_BY
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|recoveryBy
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Long
operator|.
name|class
argument_list|,
name|recoveryBy
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

