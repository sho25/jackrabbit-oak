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
name|composite
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
name|api
operator|.
name|ContentSession
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
name|api
operator|.
name|Tree
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
name|InitialContent
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
name|EditorProvider
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
name|mount
operator|.
name|MountInfoProvider
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
name|mount
operator|.
name|Mounts
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
name|OpenSecurityProvider
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
name|MockOsgi
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
name|Assert
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

begin_comment
comment|/**  * Tests the {@link PrivateStoreValidatorProvider}  */
end_comment

begin_class
specifier|public
class|class
name|PrivateStoreValidatorProviderTest
block|{
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
name|ContentRepository
name|repository
decl_stmt|;
specifier|private
name|PrivateStoreValidatorProvider
name|privateStoreValidatorProvider
init|=
operator|new
name|PrivateStoreValidatorProvider
argument_list|()
decl_stmt|;
specifier|private
name|void
name|setUp
parameter_list|(
name|String
modifier|...
name|readOnlyPaths
parameter_list|)
block|{
name|configureMountInfoProvider
argument_list|(
name|readOnlyPaths
argument_list|)
expr_stmt|;
name|repository
operator|=
operator|new
name|Oak
argument_list|()
operator|.
name|with
argument_list|(
operator|new
name|OpenSecurityProvider
argument_list|()
argument_list|)
operator|.
name|with
argument_list|(
operator|new
name|InitialContent
argument_list|()
argument_list|)
operator|.
name|with
argument_list|(
name|privateStoreValidatorProvider
argument_list|)
operator|.
name|createContentRepository
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
name|IOException
block|{
if|if
condition|(
name|repository
operator|instanceof
name|Closeable
condition|)
block|{
operator|(
operator|(
name|Closeable
operator|)
name|repository
operator|)
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDefaultMount
parameter_list|()
throws|throws
name|Exception
block|{
name|setUp
argument_list|()
expr_stmt|;
name|ContentSession
name|s
init|=
name|repository
operator|.
name|login
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|Root
name|r
init|=
name|s
operator|.
name|getLatestRoot
argument_list|()
decl_stmt|;
name|Tree
name|t
init|=
name|r
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
operator|.
name|addChild
argument_list|(
literal|"test"
argument_list|)
decl_stmt|;
name|t
operator|.
name|addChild
argument_list|(
literal|"node1"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"jcr:primaryType"
argument_list|,
literal|"nt:base"
argument_list|)
expr_stmt|;
name|t
operator|.
name|addChild
argument_list|(
literal|"node2"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"jcr:primaryType"
argument_list|,
literal|"nt:base"
argument_list|)
expr_stmt|;
name|t
operator|.
name|addChild
argument_list|(
literal|"node3"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"jcr:primaryType"
argument_list|,
literal|"nt:base"
argument_list|)
expr_stmt|;
name|r
operator|.
name|commit
argument_list|()
expr_stmt|;
name|t
operator|.
name|getChild
argument_list|(
literal|"node1"
argument_list|)
operator|.
name|removeProperty
argument_list|(
literal|"jcr:primaryType"
argument_list|)
expr_stmt|;
name|r
operator|.
name|commit
argument_list|()
expr_stmt|;
name|t
operator|.
name|getChild
argument_list|(
literal|"node1"
argument_list|)
operator|.
name|remove
argument_list|()
expr_stmt|;
name|r
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testReadOnlyMounts
parameter_list|()
throws|throws
name|Exception
block|{
comment|// default mount info provider to setup some content
name|setUp
argument_list|()
expr_stmt|;
name|ContentSession
name|s
init|=
name|repository
operator|.
name|login
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|Root
name|r
init|=
name|s
operator|.
name|getLatestRoot
argument_list|()
decl_stmt|;
name|Tree
name|t
init|=
name|r
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
operator|.
name|addChild
argument_list|(
literal|"content"
argument_list|)
decl_stmt|;
name|t
operator|.
name|addChild
argument_list|(
literal|"node1"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"jcr:primaryType"
argument_list|,
literal|"nt:base"
argument_list|)
expr_stmt|;
name|Tree
name|readonlyRoot
init|=
name|t
operator|.
name|addChild
argument_list|(
literal|"readonly"
argument_list|)
decl_stmt|;
name|readonlyRoot
operator|.
name|setProperty
argument_list|(
literal|"jcr:primaryType"
argument_list|,
literal|"nt:base"
argument_list|)
expr_stmt|;
name|readonlyRoot
operator|.
name|addChild
argument_list|(
literal|"readonlyChild"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"jcr:primaryType"
argument_list|,
literal|"nt:base"
argument_list|)
expr_stmt|;
name|r
operator|.
name|commit
argument_list|()
expr_stmt|;
comment|// register a different mount info provider
name|configureMountInfoProvider
argument_list|(
literal|"/content/readonly"
argument_list|)
expr_stmt|;
comment|// commits under /content/readonly should now fail
name|s
operator|=
name|repository
operator|.
name|login
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
comment|// changes that are not under the read-only mount should work
name|r
operator|=
name|s
operator|.
name|getLatestRoot
argument_list|()
expr_stmt|;
name|t
operator|=
name|r
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
operator|.
name|addChild
argument_list|(
literal|"content"
argument_list|)
expr_stmt|;
name|t
operator|.
name|addChild
argument_list|(
literal|"node2"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"jcr:primaryType"
argument_list|,
literal|"nt:base"
argument_list|)
expr_stmt|;
name|r
operator|.
name|commit
argument_list|()
expr_stmt|;
comment|// changes under the read-only mount should fail
name|readonlyRoot
operator|=
name|t
operator|.
name|getChild
argument_list|(
literal|"readonly"
argument_list|)
expr_stmt|;
name|readonlyRoot
operator|.
name|setProperty
argument_list|(
literal|"testProp"
argument_list|,
literal|"test"
argument_list|)
expr_stmt|;
try|try
block|{
name|r
operator|.
name|commit
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"Commit to read-only mount should fail!"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|ignore
parameter_list|)
block|{         }
name|r
operator|.
name|refresh
argument_list|()
expr_stmt|;
name|readonlyRoot
operator|=
name|t
operator|.
name|getChild
argument_list|(
literal|"readonly"
argument_list|)
expr_stmt|;
name|readonlyRoot
operator|.
name|getChild
argument_list|(
literal|"readonlyChild"
argument_list|)
operator|.
name|remove
argument_list|()
expr_stmt|;
try|try
block|{
name|r
operator|.
name|commit
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"Commit to read-only mount should fail!"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|ignore
parameter_list|)
block|{         }
block|}
annotation|@
name|Test
specifier|public
name|void
name|testValidatorServiceRegistered
parameter_list|()
block|{
comment|// test service registration, there should be a service for the PrivateStoreValidatorProvider
name|MountInfoProvider
name|mountInfoProvider
init|=
name|createMountInfoProvider
argument_list|(
literal|"/content/readonly"
argument_list|)
decl_stmt|;
name|context
operator|.
name|registerService
argument_list|(
name|MountInfoProvider
operator|.
name|class
argument_list|,
name|mountInfoProvider
argument_list|)
expr_stmt|;
name|registerValidatorProvider
argument_list|(
name|privateStoreValidatorProvider
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|EditorProvider
name|validator
init|=
name|context
operator|.
name|getService
argument_list|(
name|EditorProvider
operator|.
name|class
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"No PrivateStoreValidatorProvider available!"
argument_list|,
name|validator
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|validator
operator|instanceof
name|PrivateStoreValidatorProvider
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
operator|(
operator|(
name|PrivateStoreValidatorProvider
operator|)
name|validator
operator|)
operator|.
name|isFailOnDetection
argument_list|()
argument_list|)
expr_stmt|;
name|MockOsgi
operator|.
name|deactivate
argument_list|(
name|privateStoreValidatorProvider
argument_list|,
name|context
operator|.
name|bundleContext
argument_list|()
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|context
operator|.
name|getService
argument_list|(
name|EditorProvider
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testValidatorServiceNotRegistered
parameter_list|()
block|{
comment|// test service registration, for default mount there should be no service for the validator provider
name|MountInfoProvider
name|mountInfoProvider
init|=
name|createMountInfoProvider
argument_list|()
decl_stmt|;
name|context
operator|.
name|registerService
argument_list|(
name|MountInfoProvider
operator|.
name|class
argument_list|,
name|mountInfoProvider
argument_list|)
expr_stmt|;
name|registerValidatorProvider
argument_list|(
name|privateStoreValidatorProvider
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|EditorProvider
name|validator
init|=
name|context
operator|.
name|getService
argument_list|(
name|EditorProvider
operator|.
name|class
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
literal|"No PrivateStoreValidatorProvider should be registered for default mounts!"
argument_list|,
name|validator
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|registerValidatorProvider
parameter_list|(
name|PrivateStoreValidatorProvider
name|validatorProvider
parameter_list|,
name|boolean
name|failOnDetection
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|propMap
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|propMap
operator|.
name|put
argument_list|(
literal|"failOnDetection"
argument_list|,
name|failOnDetection
argument_list|)
expr_stmt|;
name|MockOsgi
operator|.
name|injectServices
argument_list|(
name|validatorProvider
argument_list|,
name|context
operator|.
name|bundleContext
argument_list|()
argument_list|)
expr_stmt|;
name|MockOsgi
operator|.
name|activate
argument_list|(
name|validatorProvider
argument_list|,
name|context
operator|.
name|bundleContext
argument_list|()
argument_list|,
name|propMap
argument_list|)
expr_stmt|;
block|}
comment|/**      * Register a {@link MountInfoProvider} service      * If the given path array is empty, the {@code MountInfoProvider.DEFAULT} will be registered.      *      * @param readOnlyPaths - contains the string paths mounted on a read-only store      */
specifier|private
name|void
name|configureMountInfoProvider
parameter_list|(
name|String
modifier|...
name|readOnlyPaths
parameter_list|)
block|{
name|MountInfoProvider
name|mountInfoProvider
init|=
name|createMountInfoProvider
argument_list|(
name|readOnlyPaths
argument_list|)
decl_stmt|;
name|privateStoreValidatorProvider
operator|.
name|setMountInfoProvider
argument_list|(
name|mountInfoProvider
argument_list|)
expr_stmt|;
name|privateStoreValidatorProvider
operator|.
name|setFailOnDetection
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
specifier|private
name|MountInfoProvider
name|createMountInfoProvider
parameter_list|(
name|String
modifier|...
name|readOnlyPaths
parameter_list|)
block|{
name|MountInfoProvider
name|mountInfoProvider
init|=
name|Mounts
operator|.
name|defaultMountInfoProvider
argument_list|()
decl_stmt|;
if|if
condition|(
name|readOnlyPaths
operator|.
name|length
operator|>
literal|0
condition|)
block|{
name|mountInfoProvider
operator|=
name|Mounts
operator|.
name|newBuilder
argument_list|()
operator|.
name|readOnlyMount
argument_list|(
literal|"readOnly"
argument_list|,
name|readOnlyPaths
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
block|}
return|return
name|mountInfoProvider
return|;
block|}
block|}
end_class

end_unit

