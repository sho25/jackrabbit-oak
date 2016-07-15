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
name|core
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
name|core
operator|.
name|query
operator|.
name|QueryHandler
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
name|core
operator|.
name|query
operator|.
name|lucene
operator|.
name|SearchIndex
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
name|index
operator|.
name|IndexReader
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|RepositoryException
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

begin_class
specifier|public
specifier|final
class|class
name|IndexAccessor
block|{
specifier|private
name|IndexAccessor
parameter_list|()
block|{     }
specifier|public
specifier|static
name|IndexReader
name|getReader
parameter_list|(
name|RepositoryContext
name|ctx
parameter_list|)
throws|throws
name|RepositoryException
throws|,
name|IOException
block|{
name|RepositoryImpl
name|repo
init|=
name|ctx
operator|.
name|getRepository
argument_list|()
decl_stmt|;
name|SearchManager
name|searchMgr
init|=
name|repo
operator|.
name|getSearchManager
argument_list|(
name|ctx
operator|.
name|getRepositoryConfig
argument_list|()
operator|.
name|getDefaultWorkspaceName
argument_list|()
argument_list|)
decl_stmt|;
name|QueryHandler
name|handler
init|=
name|searchMgr
operator|.
name|getQueryHandler
argument_list|()
decl_stmt|;
name|SearchIndex
name|index
init|=
operator|(
name|SearchIndex
operator|)
name|handler
decl_stmt|;
return|return
name|index
operator|.
name|getIndexReader
argument_list|()
return|;
block|}
block|}
end_class

end_unit

