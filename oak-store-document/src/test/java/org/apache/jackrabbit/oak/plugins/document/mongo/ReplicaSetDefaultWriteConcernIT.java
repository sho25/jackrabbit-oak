begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *   http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|Collection
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
name|DocumentMKBuilderProvider
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
name|LeaseCheckMode
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
name|MongoUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Rule
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
name|assertTrue
import|;
end_import

begin_class
specifier|public
class|class
name|ReplicaSetDefaultWriteConcernIT
block|{
annotation|@
name|Rule
specifier|public
name|MongodProcessFactory
name|mongodProcessFactory
init|=
operator|new
name|MongodProcessFactory
argument_list|()
decl_stmt|;
annotation|@
name|Rule
specifier|public
name|DocumentMKBuilderProvider
name|builderProvider
init|=
operator|new
name|DocumentMKBuilderProvider
argument_list|()
decl_stmt|;
specifier|private
name|Map
argument_list|<
name|Integer
argument_list|,
name|MongodProcess
argument_list|>
name|executables
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|before
parameter_list|()
throws|throws
name|IOException
block|{
name|executables
operator|.
name|putAll
argument_list|(
name|mongodProcessFactory
operator|.
name|startReplicaSet
argument_list|(
literal|"rs"
argument_list|,
literal|3
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|majorityWriteConcern
parameter_list|()
block|{
name|String
name|uri
init|=
literal|"mongodb://"
operator|+
name|MongodProcessFactory
operator|.
name|localhost
argument_list|(
name|executables
operator|.
name|keySet
argument_list|()
argument_list|)
decl_stmt|;
name|DocumentNodeStore
name|ns
init|=
name|builderProvider
operator|.
name|newBuilder
argument_list|()
operator|.
name|setLeaseCheckMode
argument_list|(
name|LeaseCheckMode
operator|.
name|DISABLED
argument_list|)
operator|.
name|setMongoDB
argument_list|(
name|uri
argument_list|,
name|MongoUtils
operator|.
name|DB
argument_list|,
literal|0
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|DocumentStore
name|store
init|=
name|ns
operator|.
name|getDocumentStore
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|store
operator|instanceof
name|MongoDocumentStore
argument_list|)
expr_stmt|;
name|MongoDocumentStore
name|mds
init|=
operator|(
name|MongoDocumentStore
operator|)
name|store
decl_stmt|;
name|WriteConcern
name|wc
init|=
name|mds
operator|.
name|getDBCollection
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|)
operator|.
name|getWriteConcern
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|WriteConcern
operator|.
name|MAJORITY
argument_list|,
name|wc
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit
