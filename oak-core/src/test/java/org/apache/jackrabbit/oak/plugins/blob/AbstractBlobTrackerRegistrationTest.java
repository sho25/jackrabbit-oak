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
name|assertNotEquals
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
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
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
name|javax
operator|.
name|annotation
operator|.
name|Nullable
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
name|base
operator|.
name|Predicate
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
name|ImmutableList
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
name|Files
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
name|blob
operator|.
name|datastore
operator|.
name|BlobTracker
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
name|blob
operator|.
name|datastore
operator|.
name|DataStoreBlobStore
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
name|blob
operator|.
name|datastore
operator|.
name|DataStoreUtils
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

begin_comment
comment|/**  * Tests OSGi registration for {@link BlobTrackingStore}.  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|AbstractBlobTrackerRegistrationTest
block|{
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
name|Rule
specifier|public
name|OsgiContext
name|context
init|=
operator|new
name|OsgiContext
argument_list|()
decl_stmt|;
specifier|protected
name|String
name|repoHome
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
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
name|repoHome
operator|=
name|folder
operator|.
name|newFolder
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
expr_stmt|;
block|}
annotation|@
name|After
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
block|}
annotation|@
name|Test
specifier|public
name|void
name|registerBlobTrackingStore
parameter_list|()
throws|throws
name|Exception
block|{
name|registerTrackingBlobStore
argument_list|()
expr_stmt|;
name|registerNodeStoreService
argument_list|()
expr_stmt|;
name|assertServiceActivated
argument_list|()
expr_stmt|;
name|BlobStore
name|blobStore
init|=
name|context
operator|.
name|getService
argument_list|(
name|BlobStore
operator|.
name|class
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|blobStore
operator|instanceof
name|BlobTrackingStore
argument_list|)
expr_stmt|;
name|BlobTrackingStore
name|trackingStore
init|=
operator|(
name|BlobTrackingStore
operator|)
name|blobStore
decl_stmt|;
name|assertNotNull
argument_list|(
name|trackingStore
operator|.
name|getTracker
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|reRegisterBlobTrackingStore
parameter_list|()
throws|throws
name|Exception
block|{
name|registerTrackingBlobStore
argument_list|()
expr_stmt|;
name|registerNodeStoreService
argument_list|()
expr_stmt|;
name|assertServiceActivated
argument_list|()
expr_stmt|;
name|BlobStore
name|blobStore
init|=
name|context
operator|.
name|getService
argument_list|(
name|BlobStore
operator|.
name|class
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|blobStore
operator|instanceof
name|BlobTrackingStore
argument_list|)
expr_stmt|;
name|BlobTrackingStore
name|trackingStore
init|=
operator|(
name|BlobTrackingStore
operator|)
name|blobStore
decl_stmt|;
name|assertNotNull
argument_list|(
name|trackingStore
operator|.
name|getTracker
argument_list|()
argument_list|)
expr_stmt|;
name|BlobTracker
name|oldTracker
init|=
name|trackingStore
operator|.
name|getTracker
argument_list|()
decl_stmt|;
name|unregisterNodeStoreService
argument_list|()
expr_stmt|;
name|registerNodeStoreService
argument_list|()
expr_stmt|;
name|blobStore
operator|=
name|context
operator|.
name|getService
argument_list|(
name|BlobStore
operator|.
name|class
argument_list|)
expr_stmt|;
name|trackingStore
operator|=
operator|(
name|BlobTrackingStore
operator|)
name|blobStore
expr_stmt|;
name|BlobTracker
name|newTracker
init|=
name|trackingStore
operator|.
name|getTracker
argument_list|()
decl_stmt|;
name|assertNotEquals
argument_list|(
name|oldTracker
argument_list|,
name|newTracker
argument_list|)
expr_stmt|;
name|assertTrackerReinitialized
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|assertTrackerReinitialized
parameter_list|()
block|{
name|File
name|blobIdFiles
init|=
operator|new
name|File
argument_list|(
name|repoHome
argument_list|,
literal|"blobids"
argument_list|)
decl_stmt|;
name|ImmutableList
argument_list|<
name|File
argument_list|>
name|files
init|=
name|Files
operator|.
name|fileTreeTraverser
argument_list|()
operator|.
name|postOrderTraversal
argument_list|(
name|blobIdFiles
argument_list|)
operator|.
name|filter
argument_list|(
operator|new
name|Predicate
argument_list|<
name|File
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|apply
parameter_list|(
annotation|@
name|Nullable
name|File
name|input
parameter_list|)
block|{
return|return
name|input
operator|.
name|getAbsolutePath
argument_list|()
operator|.
name|endsWith
argument_list|(
literal|".process"
argument_list|)
return|;
block|}
block|}
argument_list|)
operator|.
name|toList
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|files
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
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
block|}
specifier|private
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
block|}
specifier|protected
name|ServiceRegistration
name|blobStore
decl_stmt|;
specifier|protected
name|void
name|registerTrackingBlobStore
parameter_list|()
throws|throws
name|Exception
block|{
name|DataStoreBlobStore
name|blobStore
init|=
name|DataStoreUtils
operator|.
name|getBlobStore
argument_list|(
name|repoHome
argument_list|)
decl_stmt|;
name|this
operator|.
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
name|blobStore
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
specifier|protected
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
specifier|abstract
name|void
name|registerNodeStoreService
parameter_list|()
function_decl|;
specifier|protected
specifier|abstract
name|void
name|unregisterNodeStoreService
parameter_list|()
function_decl|;
block|}
end_class

end_unit

