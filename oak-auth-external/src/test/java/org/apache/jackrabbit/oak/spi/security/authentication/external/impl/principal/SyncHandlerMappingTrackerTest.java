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
name|spi
operator|.
name|security
operator|.
name|authentication
operator|.
name|external
operator|.
name|impl
operator|.
name|principal
package|;
end_package

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
name|ImmutableSet
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
name|Iterables
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
name|security
operator|.
name|authentication
operator|.
name|external
operator|.
name|impl
operator|.
name|SyncHandlerMapping
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
name|Test
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
name|BundleContext
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
name|ServiceReference
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
name|spi
operator|.
name|security
operator|.
name|authentication
operator|.
name|external
operator|.
name|impl
operator|.
name|SyncHandlerMapping
operator|.
name|PARAM_IDP_NAME
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
name|spi
operator|.
name|security
operator|.
name|authentication
operator|.
name|external
operator|.
name|impl
operator|.
name|SyncHandlerMapping
operator|.
name|PARAM_SYNC_HANDLER_NAME
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
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|when
import|;
end_import

begin_class
specifier|public
class|class
name|SyncHandlerMappingTrackerTest
block|{
specifier|private
specifier|final
name|BundleContext
name|bundleContext
init|=
name|mock
argument_list|(
name|BundleContext
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|ServiceReference
name|ref
init|=
name|mock
argument_list|(
name|ServiceReference
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|SyncHandlerMapping
name|service
init|=
name|mock
argument_list|(
name|SyncHandlerMapping
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|SyncHandlerMappingTracker
name|tracker
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|before
parameter_list|()
block|{
name|tracker
operator|=
operator|new
name|SyncHandlerMappingTracker
argument_list|(
name|bundleContext
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAddingServiceWithoutProperties
parameter_list|()
block|{
name|tracker
operator|.
name|addingService
argument_list|(
name|ref
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|Iterables
operator|.
name|isEmpty
argument_list|(
name|tracker
operator|.
name|getIdpNames
argument_list|(
literal|"testSH"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAddingServiceWithIdpProp
parameter_list|()
block|{
name|when
argument_list|(
name|ref
operator|.
name|getProperty
argument_list|(
name|PARAM_IDP_NAME
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|"testIDP"
argument_list|)
expr_stmt|;
name|tracker
operator|.
name|addingService
argument_list|(
name|ref
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|Iterables
operator|.
name|isEmpty
argument_list|(
name|tracker
operator|.
name|getIdpNames
argument_list|(
literal|"testSH"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAddingServiceWithSyncHandlerProp
parameter_list|()
block|{
name|when
argument_list|(
name|ref
operator|.
name|getProperty
argument_list|(
name|PARAM_SYNC_HANDLER_NAME
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|"testSH"
argument_list|)
expr_stmt|;
name|tracker
operator|.
name|addingService
argument_list|(
name|ref
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|Iterables
operator|.
name|isEmpty
argument_list|(
name|tracker
operator|.
name|getIdpNames
argument_list|(
literal|"testSH"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAddingServiceWithProperties
parameter_list|()
block|{
name|when
argument_list|(
name|ref
operator|.
name|getProperty
argument_list|(
name|PARAM_SYNC_HANDLER_NAME
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|"testSH"
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|ref
operator|.
name|getProperty
argument_list|(
name|PARAM_IDP_NAME
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|"testIDP"
argument_list|)
expr_stmt|;
name|tracker
operator|.
name|addingService
argument_list|(
name|ref
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ImmutableSet
operator|.
name|of
argument_list|(
literal|"testIDP"
argument_list|)
argument_list|,
name|ImmutableSet
operator|.
name|copyOf
argument_list|(
name|tracker
operator|.
name|getIdpNames
argument_list|(
literal|"testSH"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|ServiceReference
name|ref2
init|=
name|mock
argument_list|(
name|ServiceReference
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|ref2
operator|.
name|getProperty
argument_list|(
name|PARAM_SYNC_HANDLER_NAME
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|"testSH-2"
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|ref2
operator|.
name|getProperty
argument_list|(
name|PARAM_IDP_NAME
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|"testIDP-2"
argument_list|)
expr_stmt|;
name|tracker
operator|.
name|addingService
argument_list|(
name|ref2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ImmutableSet
operator|.
name|of
argument_list|(
literal|"testIDP"
argument_list|)
argument_list|,
name|ImmutableSet
operator|.
name|copyOf
argument_list|(
name|tracker
operator|.
name|getIdpNames
argument_list|(
literal|"testSH"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ImmutableSet
operator|.
name|of
argument_list|(
literal|"testIDP-2"
argument_list|)
argument_list|,
name|ImmutableSet
operator|.
name|copyOf
argument_list|(
name|tracker
operator|.
name|getIdpNames
argument_list|(
literal|"testSH-2"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|ServiceReference
name|ref3
init|=
name|mock
argument_list|(
name|ServiceReference
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|ref3
operator|.
name|getProperty
argument_list|(
name|PARAM_SYNC_HANDLER_NAME
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|"testSH"
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|ref3
operator|.
name|getProperty
argument_list|(
name|PARAM_IDP_NAME
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|"testIDP-3"
argument_list|)
expr_stmt|;
name|tracker
operator|.
name|addingService
argument_list|(
name|ref3
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ImmutableSet
operator|.
name|of
argument_list|(
literal|"testIDP"
argument_list|,
literal|"testIDP-3"
argument_list|)
argument_list|,
name|ImmutableSet
operator|.
name|copyOf
argument_list|(
name|tracker
operator|.
name|getIdpNames
argument_list|(
literal|"testSH"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ImmutableSet
operator|.
name|of
argument_list|(
literal|"testIDP-2"
argument_list|)
argument_list|,
name|ImmutableSet
operator|.
name|copyOf
argument_list|(
name|tracker
operator|.
name|getIdpNames
argument_list|(
literal|"testSH-2"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testModifiedServiceWithoutProperties
parameter_list|()
block|{
name|tracker
operator|.
name|modifiedService
argument_list|(
name|ref
argument_list|,
name|service
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|Iterables
operator|.
name|isEmpty
argument_list|(
name|tracker
operator|.
name|getIdpNames
argument_list|(
literal|"testSH"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testModifiedServiceWithIdpProp
parameter_list|()
block|{
name|when
argument_list|(
name|ref
operator|.
name|getProperty
argument_list|(
name|PARAM_IDP_NAME
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|"testIDP"
argument_list|)
expr_stmt|;
name|tracker
operator|.
name|modifiedService
argument_list|(
name|ref
argument_list|,
name|service
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|Iterables
operator|.
name|isEmpty
argument_list|(
name|tracker
operator|.
name|getIdpNames
argument_list|(
literal|"testSH"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testModifiedServiceWithSyncHandlerProp
parameter_list|()
block|{
name|when
argument_list|(
name|ref
operator|.
name|getProperty
argument_list|(
name|PARAM_SYNC_HANDLER_NAME
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|"testSH"
argument_list|)
expr_stmt|;
name|tracker
operator|.
name|modifiedService
argument_list|(
name|ref
argument_list|,
name|service
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|Iterables
operator|.
name|isEmpty
argument_list|(
name|tracker
operator|.
name|getIdpNames
argument_list|(
literal|"testSH"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testModifiedServiceWithProperties
parameter_list|()
block|{
name|when
argument_list|(
name|ref
operator|.
name|getProperty
argument_list|(
name|PARAM_SYNC_HANDLER_NAME
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|"testSH"
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|ref
operator|.
name|getProperty
argument_list|(
name|PARAM_IDP_NAME
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|"testIDP"
argument_list|)
expr_stmt|;
name|tracker
operator|.
name|addingService
argument_list|(
name|ref
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|ref
operator|.
name|getProperty
argument_list|(
name|PARAM_SYNC_HANDLER_NAME
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|"testSH"
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|ref
operator|.
name|getProperty
argument_list|(
name|PARAM_IDP_NAME
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|"testIDP-2"
argument_list|)
expr_stmt|;
name|tracker
operator|.
name|modifiedService
argument_list|(
name|ref
argument_list|,
name|service
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ImmutableSet
operator|.
name|of
argument_list|(
literal|"testIDP-2"
argument_list|)
argument_list|,
name|ImmutableSet
operator|.
name|copyOf
argument_list|(
name|tracker
operator|.
name|getIdpNames
argument_list|(
literal|"testSH"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|ref
operator|.
name|getProperty
argument_list|(
name|PARAM_SYNC_HANDLER_NAME
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|"testSH-3"
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|ref
operator|.
name|getProperty
argument_list|(
name|PARAM_IDP_NAME
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|"testIDP-3"
argument_list|)
expr_stmt|;
name|tracker
operator|.
name|modifiedService
argument_list|(
name|ref
argument_list|,
name|service
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|Iterables
operator|.
name|isEmpty
argument_list|(
name|tracker
operator|.
name|getIdpNames
argument_list|(
literal|"testSH"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ImmutableSet
operator|.
name|of
argument_list|(
literal|"testIDP-3"
argument_list|)
argument_list|,
name|ImmutableSet
operator|.
name|copyOf
argument_list|(
name|tracker
operator|.
name|getIdpNames
argument_list|(
literal|"testSH-3"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRemovedService
parameter_list|()
block|{
name|when
argument_list|(
name|ref
operator|.
name|getProperty
argument_list|(
name|PARAM_SYNC_HANDLER_NAME
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|"testSH"
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|ref
operator|.
name|getProperty
argument_list|(
name|PARAM_IDP_NAME
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|"testIDP"
argument_list|)
expr_stmt|;
name|tracker
operator|.
name|addingService
argument_list|(
name|ref
argument_list|)
expr_stmt|;
name|tracker
operator|.
name|removedService
argument_list|(
name|ref
argument_list|,
name|service
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|Iterables
operator|.
name|isEmpty
argument_list|(
name|tracker
operator|.
name|getIdpNames
argument_list|(
literal|"testSH"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

