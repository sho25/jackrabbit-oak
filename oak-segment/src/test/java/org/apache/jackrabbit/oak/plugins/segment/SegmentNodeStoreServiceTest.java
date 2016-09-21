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
name|segment
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
name|Assert
operator|.
name|assertNotNull
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
name|assertNull
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|mock
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
name|spi
operator|.
name|blob
operator|.
name|BlobStore
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
name|spi
operator|.
name|state
operator|.
name|NodeStoreProvider
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
name|junit
operator|.
name|OsgiContext
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
import|import
name|org
operator|.
name|junit
operator|.
name|rules
operator|.
name|TemporaryFolder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|osgi
operator|.
name|framework
operator|.
name|ServiceRegistration
import|;
end_import

begin_class
specifier|public
class|class
name|SegmentNodeStoreServiceTest
block|{
annotation|@
name|Rule
specifier|public
name|OsgiContext
name|context
init|=
operator|new
name|OsgiContext
argument_list|()
decl_stmt|;
annotation|@
name|Rule
specifier|public
name|TemporaryFolder
name|folder
init|=
operator|new
name|TemporaryFolder
argument_list|(
operator|new
name|File
argument_list|(
literal|"target"
argument_list|)
argument_list|)
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|setUp
parameter_list|()
block|{
name|context
operator|.
name|registerService
argument_list|(
name|StatisticsProvider
operator|.
name|class
argument_list|,
name|StatisticsProvider
operator|.
name|NOOP
argument_list|)
expr_stmt|;
block|}
comment|/**      * A NodeStore service should be registered when a BlobStore service is not      * available and the "customBlobStore" configuration property is false.      */
annotation|@
name|Test
specifier|public
name|void
name|testNoCustomBlobStoreWithoutBlobStore
parameter_list|()
block|{
name|registerSegmentNodeStoreService
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|assertServiceActivated
argument_list|()
expr_stmt|;
name|unregisterSegmentNodeStoreService
argument_list|()
expr_stmt|;
block|}
comment|/**      * A NodeStore service should be registered when a BlobStore service is not      * available but the "customBlobStore" configuration property is false.      */
annotation|@
name|Test
specifier|public
name|void
name|testNoCustomBlobStoreWithBlobStore
parameter_list|()
block|{
name|registerBlobStore
argument_list|()
expr_stmt|;
name|registerSegmentNodeStoreService
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|assertServiceActivated
argument_list|()
expr_stmt|;
name|unregisterSegmentNodeStoreService
argument_list|()
expr_stmt|;
name|unregisterBlobStore
argument_list|()
expr_stmt|;
block|}
comment|/**      * A NodeStore service should not be registered when the "customBlobStore"      * configuration property is true but a BlobStore service is not available.      */
annotation|@
name|Test
specifier|public
name|void
name|testUseCustomBlobStoreWithoutBlobStore
parameter_list|()
block|{
name|registerSegmentNodeStoreService
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|assertServiceNotActivated
argument_list|()
expr_stmt|;
name|unregisterSegmentNodeStoreService
argument_list|()
expr_stmt|;
block|}
comment|/**      * A NodeStore service should be registered when the "customBlobStore"      * configuration property is true and a BlobStore service is available.      */
annotation|@
name|Test
specifier|public
name|void
name|testUseCustomBlobStoreWithBlobStore
parameter_list|()
block|{
name|registerBlobStore
argument_list|()
expr_stmt|;
name|registerSegmentNodeStoreService
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|assertServiceActivated
argument_list|()
expr_stmt|;
name|unregisterSegmentNodeStoreService
argument_list|()
expr_stmt|;
name|unregisterBlobStore
argument_list|()
expr_stmt|;
block|}
comment|/**      * A NodeStore service should be registered when the "customBlobStore"      * configuration property is true and a BlobStore service becomes      * dynamically available.      */
annotation|@
name|Test
specifier|public
name|void
name|testUseCustomBlobStoreWithDynamicBlobStoreActivation
parameter_list|()
block|{
name|registerSegmentNodeStoreService
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|assertServiceNotActivated
argument_list|()
expr_stmt|;
name|registerBlobStore
argument_list|()
expr_stmt|;
name|assertServiceActivated
argument_list|()
expr_stmt|;
name|unregisterSegmentNodeStoreService
argument_list|()
expr_stmt|;
name|unregisterBlobStore
argument_list|()
expr_stmt|;
block|}
comment|/**      * A NodeStore service should be unregistered when the "customBlobStore"      * configuration property is true and a BlobStore service becomes      * dynamically unavailable.      */
annotation|@
name|Test
specifier|public
name|void
name|testUseCustomBlobStoreWithDynamicBlobStoreDeactivation
parameter_list|()
block|{
name|registerBlobStore
argument_list|()
expr_stmt|;
name|registerSegmentNodeStoreService
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|assertServiceActivated
argument_list|()
expr_stmt|;
name|unregisterBlobStore
argument_list|()
expr_stmt|;
name|assertServiceNotActivated
argument_list|()
expr_stmt|;
name|unregisterSegmentNodeStoreService
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|nodeStoreProvider
parameter_list|()
throws|throws
name|Exception
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
name|SegmentNodeStoreService
operator|.
name|SECONDARY_STORE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|properties
operator|.
name|put
argument_list|(
name|SegmentNodeStoreService
operator|.
name|DIRECTORY
argument_list|,
name|folder
operator|.
name|getRoot
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|context
operator|.
name|registerService
argument_list|(
name|BlobStore
operator|.
name|class
argument_list|,
operator|new
name|MemoryBlobStore
argument_list|()
argument_list|)
expr_stmt|;
name|segmentNodeStoreService
operator|=
name|context
operator|.
name|registerInjectActivateService
argument_list|(
operator|new
name|SegmentNodeStoreService
argument_list|()
argument_list|,
name|properties
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|context
operator|.
name|getService
argument_list|(
name|NodeStore
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|context
operator|.
name|getService
argument_list|(
name|NodeStoreProvider
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|SegmentNodeStoreService
name|segmentNodeStoreService
decl_stmt|;
specifier|protected
name|void
name|registerSegmentNodeStoreService
parameter_list|(
name|boolean
name|customBlobStore
parameter_list|)
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
name|SegmentNodeStoreService
operator|.
name|CUSTOM_BLOB_STORE
argument_list|,
name|customBlobStore
argument_list|)
expr_stmt|;
name|properties
operator|.
name|put
argument_list|(
name|SegmentNodeStoreService
operator|.
name|DIRECTORY
argument_list|,
name|folder
operator|.
name|getRoot
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|segmentNodeStoreService
operator|=
name|context
operator|.
name|registerInjectActivateService
argument_list|(
operator|new
name|SegmentNodeStoreService
argument_list|()
argument_list|,
name|properties
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|unregisterSegmentNodeStoreService
parameter_list|()
block|{
name|deactivate
argument_list|(
name|segmentNodeStoreService
argument_list|)
expr_stmt|;
block|}
specifier|private
name|ServiceRegistration
name|blobStore
decl_stmt|;
specifier|private
name|void
name|registerBlobStore
parameter_list|()
block|{
name|blobStore
operator|=
name|context
operator|.
name|bundleContext
argument_list|()
operator|.
name|registerService
argument_list|(
name|BlobStore
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
name|mock
argument_list|(
name|BlobStore
operator|.
name|class
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|unregisterBlobStore
parameter_list|()
block|{
name|blobStore
operator|.
name|unregister
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|void
name|assertServiceActivated
parameter_list|()
block|{
name|assertNotNull
argument_list|(
name|context
operator|.
name|getService
argument_list|(
name|NodeStore
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|context
operator|.
name|getService
argument_list|(
name|SegmentStoreProvider
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|assertServiceNotActivated
parameter_list|()
block|{
name|assertNull
argument_list|(
name|context
operator|.
name|getService
argument_list|(
name|NodeStore
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|context
operator|.
name|getService
argument_list|(
name|SegmentStoreProvider
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

