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
name|blob
package|;
end_package

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
name|DocumentNodeStoreService
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
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|stats
operator|.
name|StatisticsProvider
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
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
name|BeforeClass
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
name|Maps
operator|.
name|newHashMap
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|sling
operator|.
name|testing
operator|.
name|mock
operator|.
name|osgi
operator|.
name|MockOsgi
operator|.
name|deactivate
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assume
operator|.
name|assumeTrue
import|;
end_import

begin_comment
comment|/**  * Tests OSGi registration for {@link BlobTrackingStore} in {@link DocumentNodeStoreService}.  */
end_comment

begin_class
specifier|public
class|class
name|DocumentBlobTrackerRegistrationTest
extends|extends
name|AbstractBlobTrackerRegistrationTest
block|{
specifier|private
name|DocumentNodeStoreService
name|service
decl_stmt|;
annotation|@
name|BeforeClass
specifier|public
specifier|static
name|void
name|assumptions
parameter_list|()
block|{
name|assumeTrue
argument_list|(
name|MongoUtils
operator|.
name|isAvailable
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
annotation|@
name|Override
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|unregisterNodeStoreService
argument_list|()
expr_stmt|;
name|unregisterBlobStore
argument_list|()
expr_stmt|;
name|MongoUtils
operator|.
name|dropCollections
argument_list|(
name|MongoUtils
operator|.
name|DB
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|registerNodeStoreService
parameter_list|()
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|properties
init|=
name|newHashMap
argument_list|()
decl_stmt|;
name|properties
operator|.
name|put
argument_list|(
name|DocumentNodeStoreService
operator|.
name|CUSTOM_BLOB_STORE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|properties
operator|.
name|put
argument_list|(
literal|"repository.home"
argument_list|,
name|repoHome
argument_list|)
expr_stmt|;
name|properties
operator|.
name|put
argument_list|(
literal|"mongouri"
argument_list|,
name|MongoUtils
operator|.
name|URL
argument_list|)
expr_stmt|;
name|properties
operator|.
name|put
argument_list|(
literal|"db"
argument_list|,
name|MongoUtils
operator|.
name|DB
argument_list|)
expr_stmt|;
name|service
operator|=
name|context
operator|.
name|registerInjectActivateService
argument_list|(
operator|new
name|DocumentNodeStoreService
argument_list|()
argument_list|,
name|properties
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|unregisterNodeStoreService
parameter_list|()
block|{
name|deactivate
argument_list|(
name|service
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

