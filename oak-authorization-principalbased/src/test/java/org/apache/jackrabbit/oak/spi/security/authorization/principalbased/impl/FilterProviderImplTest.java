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
name|authorization
operator|.
name|principalbased
operator|.
name|impl
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
name|Root
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
name|commons
operator|.
name|PathUtils
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
name|namepath
operator|.
name|NamePathMapper
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
name|security
operator|.
name|internal
operator|.
name|SecurityProviderBuilder
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
name|SecurityProvider
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
name|authorization
operator|.
name|principalbased
operator|.
name|Filter
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
name|principal
operator|.
name|PrincipalConfiguration
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
name|mockito
operator|.
name|Mockito
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
name|assertFalse
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
name|FilterProviderImplTest
block|{
specifier|private
specifier|static
specifier|final
name|String
name|PATH
init|=
literal|"/supported/path"
decl_stmt|;
annotation|@
name|Rule
specifier|public
specifier|final
name|OsgiContext
name|context
init|=
operator|new
name|OsgiContext
argument_list|()
decl_stmt|;
specifier|private
name|FilterProviderImpl
name|provider
init|=
name|AbstractPrincipalBasedTest
operator|.
name|createFilterProviderImpl
argument_list|(
name|PATH
argument_list|)
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|testHandlesPath
parameter_list|()
block|{
name|assertFalse
argument_list|(
name|provider
operator|.
name|handlesPath
argument_list|(
name|PathUtils
operator|.
name|ROOT_PATH
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|provider
operator|.
name|handlesPath
argument_list|(
name|PATH
operator|+
literal|"sibling"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|provider
operator|.
name|handlesPath
argument_list|(
name|PATH
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|provider
operator|.
name|handlesPath
argument_list|(
name|PathUtils
operator|.
name|concat
argument_list|(
name|PATH
argument_list|,
literal|"a"
argument_list|,
literal|"path"
argument_list|,
literal|"below"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetSearchRoot
parameter_list|()
block|{
name|assertEquals
argument_list|(
name|PATH
argument_list|,
name|provider
operator|.
name|getFilterRoot
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetFilter
parameter_list|()
block|{
name|SecurityProvider
name|sp
init|=
name|Mockito
operator|.
name|spy
argument_list|(
name|SecurityProviderBuilder
operator|.
name|newBuilder
argument_list|()
operator|.
name|build
argument_list|()
argument_list|)
decl_stmt|;
name|Root
name|root
init|=
name|mock
argument_list|(
name|Root
operator|.
name|class
argument_list|)
decl_stmt|;
name|Filter
name|filter
init|=
name|provider
operator|.
name|getFilter
argument_list|(
name|sp
argument_list|,
name|root
argument_list|,
name|NamePathMapper
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|filter
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|verify
argument_list|(
name|sp
argument_list|,
name|Mockito
operator|.
name|times
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|getConfiguration
argument_list|(
name|PrincipalConfiguration
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testActivate
parameter_list|()
block|{
name|FilterProviderImpl
name|fp
init|=
operator|new
name|FilterProviderImpl
argument_list|()
decl_stmt|;
name|assertNull
argument_list|(
name|fp
operator|.
name|getFilterRoot
argument_list|()
argument_list|)
expr_stmt|;
name|fp
operator|.
name|activate
argument_list|(
name|when
argument_list|(
name|mock
argument_list|(
name|FilterProviderImpl
operator|.
name|Configuration
operator|.
name|class
argument_list|)
operator|.
name|path
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|PATH
argument_list|)
operator|.
name|getMock
argument_list|()
argument_list|,
name|Collections
operator|.
name|emptyMap
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|PATH
argument_list|,
name|fp
operator|.
name|getFilterRoot
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|IllegalStateException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|testActivateEmptyPath
parameter_list|()
block|{
name|FilterProviderImpl
name|fp
init|=
operator|new
name|FilterProviderImpl
argument_list|()
decl_stmt|;
name|fp
operator|.
name|activate
argument_list|(
name|when
argument_list|(
name|mock
argument_list|(
name|FilterProviderImpl
operator|.
name|Configuration
operator|.
name|class
argument_list|)
operator|.
name|path
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|""
argument_list|)
operator|.
name|getMock
argument_list|()
argument_list|,
name|Collections
operator|.
name|emptyMap
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|IllegalStateException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|testActivateNullPath
parameter_list|()
block|{
name|FilterProviderImpl
name|fp
init|=
operator|new
name|FilterProviderImpl
argument_list|()
decl_stmt|;
name|fp
operator|.
name|activate
argument_list|(
name|when
argument_list|(
name|mock
argument_list|(
name|FilterProviderImpl
operator|.
name|Configuration
operator|.
name|class
argument_list|)
operator|.
name|path
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|null
argument_list|)
operator|.
name|getMock
argument_list|()
argument_list|,
name|Collections
operator|.
name|emptyMap
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|IllegalStateException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|testActivateRelativePath
parameter_list|()
block|{
name|FilterProviderImpl
name|fp
init|=
operator|new
name|FilterProviderImpl
argument_list|()
decl_stmt|;
name|fp
operator|.
name|activate
argument_list|(
name|when
argument_list|(
name|mock
argument_list|(
name|FilterProviderImpl
operator|.
name|Configuration
operator|.
name|class
argument_list|)
operator|.
name|path
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|"rel/path"
argument_list|)
operator|.
name|getMock
argument_list|()
argument_list|,
name|Collections
operator|.
name|emptyMap
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testModified
parameter_list|()
block|{
name|String
name|modifiedPath
init|=
literal|"/modified/path"
decl_stmt|;
name|provider
operator|.
name|modified
argument_list|(
name|when
argument_list|(
name|mock
argument_list|(
name|FilterProviderImpl
operator|.
name|Configuration
operator|.
name|class
argument_list|)
operator|.
name|path
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|modifiedPath
argument_list|)
operator|.
name|getMock
argument_list|()
argument_list|,
name|Collections
operator|.
name|emptyMap
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|modifiedPath
argument_list|,
name|provider
operator|.
name|getFilterRoot
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|RuntimeException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|testActivateServiceMissingConfiguration
parameter_list|()
block|{
name|context
operator|.
name|registerInjectActivateService
argument_list|(
operator|new
name|FilterProviderImpl
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

