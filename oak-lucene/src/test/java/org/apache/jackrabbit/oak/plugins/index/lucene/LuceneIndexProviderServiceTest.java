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
name|index
operator|.
name|lucene
package|;
end_package

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
name|index
operator|.
name|IndexEditorProvider
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
name|BackgroundObserver
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
name|Observer
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
name|query
operator|.
name|QueryIndexProvider
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|InfoStream
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

begin_class
specifier|public
class|class
name|LuceneIndexProviderServiceTest
block|{
annotation|@
name|Rule
specifier|public
specifier|final
name|TemporaryFolder
name|folder
init|=
operator|new
name|TemporaryFolder
argument_list|()
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
name|LuceneIndexProviderService
name|service
init|=
operator|new
name|LuceneIndexProviderService
argument_list|()
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|defaultSetup
parameter_list|()
throws|throws
name|Exception
block|{
name|MockOsgi
operator|.
name|activate
argument_list|(
name|service
argument_list|,
name|context
operator|.
name|bundleContext
argument_list|()
argument_list|,
name|getDefaultConfig
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|context
operator|.
name|getService
argument_list|(
name|QueryIndexProvider
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
name|Observer
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
name|IndexEditorProvider
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|LuceneIndexEditorProvider
name|editorProvider
init|=
operator|(
name|LuceneIndexEditorProvider
operator|)
name|context
operator|.
name|getService
argument_list|(
name|IndexEditorProvider
operator|.
name|class
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
name|editorProvider
operator|.
name|getIndexCopier
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"CopyOnRead should be enabled by default"
argument_list|,
name|context
operator|.
name|getService
argument_list|(
name|CopyOnReadStatsMBean
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|context
operator|.
name|getService
argument_list|(
name|Observer
operator|.
name|class
argument_list|)
operator|instanceof
name|BackgroundObserver
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|InfoStream
operator|.
name|NO_OUTPUT
argument_list|,
name|InfoStream
operator|.
name|getDefault
argument_list|()
argument_list|)
expr_stmt|;
name|MockOsgi
operator|.
name|deactivate
argument_list|(
name|service
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|disableOpenIndexAsync
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
name|config
init|=
name|getDefaultConfig
argument_list|()
decl_stmt|;
name|config
operator|.
name|put
argument_list|(
literal|"enableOpenIndexAsync"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|MockOsgi
operator|.
name|activate
argument_list|(
name|service
argument_list|,
name|context
operator|.
name|bundleContext
argument_list|()
argument_list|,
name|config
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|context
operator|.
name|getService
argument_list|(
name|Observer
operator|.
name|class
argument_list|)
operator|instanceof
name|LuceneIndexProvider
argument_list|)
expr_stmt|;
name|MockOsgi
operator|.
name|deactivate
argument_list|(
name|service
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|enableCopyOnWrite
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
name|config
init|=
name|getDefaultConfig
argument_list|()
decl_stmt|;
name|config
operator|.
name|put
argument_list|(
literal|"enableCopyOnWriteSupport"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|MockOsgi
operator|.
name|activate
argument_list|(
name|service
argument_list|,
name|context
operator|.
name|bundleContext
argument_list|()
argument_list|,
name|config
argument_list|)
expr_stmt|;
name|LuceneIndexEditorProvider
name|editorProvider
init|=
operator|(
name|LuceneIndexEditorProvider
operator|)
name|context
operator|.
name|getService
argument_list|(
name|IndexEditorProvider
operator|.
name|class
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|editorProvider
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|editorProvider
operator|.
name|getIndexCopier
argument_list|()
argument_list|)
expr_stmt|;
name|MockOsgi
operator|.
name|deactivate
argument_list|(
name|service
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|debugLogging
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
name|config
init|=
name|getDefaultConfig
argument_list|()
decl_stmt|;
name|config
operator|.
name|put
argument_list|(
literal|"debug"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|MockOsgi
operator|.
name|activate
argument_list|(
name|service
argument_list|,
name|context
operator|.
name|bundleContext
argument_list|()
argument_list|,
name|config
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|LoggingInfoStream
operator|.
name|INSTANCE
argument_list|,
name|InfoStream
operator|.
name|getDefault
argument_list|()
argument_list|)
expr_stmt|;
name|MockOsgi
operator|.
name|deactivate
argument_list|(
name|service
argument_list|)
expr_stmt|;
block|}
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|getDefaultConfig
parameter_list|()
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|config
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|config
operator|.
name|put
argument_list|(
literal|"localIndexDir"
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
return|return
name|config
return|;
block|}
block|}
end_class

end_unit

