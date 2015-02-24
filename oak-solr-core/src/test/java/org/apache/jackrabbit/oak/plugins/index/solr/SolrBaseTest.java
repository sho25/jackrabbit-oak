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
name|index
operator|.
name|solr
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|NoSuchWorkspaceException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|security
operator|.
name|auth
operator|.
name|login
operator|.
name|LoginException
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
name|plugins
operator|.
name|index
operator|.
name|IndexUpdateProvider
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
name|solr
operator|.
name|configuration
operator|.
name|OakSolrConfiguration
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
name|solr
operator|.
name|configuration
operator|.
name|nodestate
operator|.
name|NodeStateSolrServersObserver
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
name|solr
operator|.
name|index
operator|.
name|SolrIndexEditorProvider
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
name|solr
operator|.
name|query
operator|.
name|SolrQueryIndexProvider
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
name|solr
operator|.
name|util
operator|.
name|SolrIndexInitializer
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
name|nodetype
operator|.
name|write
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
name|plugins
operator|.
name|segment
operator|.
name|SegmentNodeStore
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
name|EditorHook
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
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|SolrServer
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

begin_comment
comment|/**  * Test base class for Oak-Solr  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|SolrBaseTest
block|{
specifier|protected
name|NodeStore
name|store
decl_stmt|;
specifier|protected
name|TestUtils
name|provider
decl_stmt|;
specifier|protected
name|SolrServer
name|server
decl_stmt|;
specifier|protected
name|OakSolrConfiguration
name|configuration
decl_stmt|;
specifier|protected
name|EditorHook
name|hook
decl_stmt|;
specifier|private
name|ContentRepository
name|repository
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
name|store
operator|=
operator|new
name|SegmentNodeStore
argument_list|()
expr_stmt|;
name|provider
operator|=
operator|new
name|TestUtils
argument_list|()
expr_stmt|;
name|server
operator|=
name|provider
operator|.
name|getSolrServer
argument_list|()
expr_stmt|;
name|configuration
operator|=
name|provider
operator|.
name|getConfiguration
argument_list|()
expr_stmt|;
name|hook
operator|=
operator|new
name|EditorHook
argument_list|(
operator|new
name|IndexUpdateProvider
argument_list|(
operator|new
name|SolrIndexEditorProvider
argument_list|(
name|provider
argument_list|,
name|provider
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|Oak
name|oak
init|=
operator|new
name|Oak
argument_list|()
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
operator|new
name|OpenSecurityProvider
argument_list|()
argument_list|)
operator|.
name|with
argument_list|(
operator|new
name|SolrIndexInitializer
argument_list|(
literal|false
argument_list|)
argument_list|)
comment|// synchronous
operator|.
name|with
argument_list|(
operator|new
name|SolrQueryIndexProvider
argument_list|(
name|provider
argument_list|,
name|provider
argument_list|)
argument_list|)
operator|.
name|with
argument_list|(
operator|new
name|NodeStateSolrServersObserver
argument_list|()
argument_list|)
operator|.
name|with
argument_list|(
operator|new
name|SolrIndexEditorProvider
argument_list|(
name|provider
argument_list|,
name|provider
argument_list|)
argument_list|)
decl_stmt|;
name|repository
operator|=
name|oak
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
name|Exception
block|{
if|if
condition|(
name|server
operator|!=
literal|null
operator|&&
name|server
operator|.
name|ping
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|server
operator|.
name|deleteByQuery
argument_list|(
literal|"*:*"
argument_list|)
expr_stmt|;
name|server
operator|.
name|commit
argument_list|()
expr_stmt|;
name|server
operator|=
literal|null
expr_stmt|;
block|}
block|}
specifier|protected
name|Root
name|createRoot
parameter_list|()
throws|throws
name|LoginException
throws|,
name|NoSuchWorkspaceException
block|{
return|return
name|repository
operator|.
name|login
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
operator|.
name|getLatestRoot
argument_list|()
return|;
block|}
block|}
end_class

end_unit

