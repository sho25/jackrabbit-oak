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
name|benchmark
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
name|ImmutableSet
operator|.
name|of
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
name|jcr
operator|.
name|Repository
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
name|fixture
operator|.
name|JcrCreator
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
name|fixture
operator|.
name|OakRepositoryFixture
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
name|fixture
operator|.
name|RepositoryFixture
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
name|jcr
operator|.
name|Jcr
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
name|lucene
operator|.
name|LuceneIndexEditorProvider
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
name|lucene
operator|.
name|LuceneIndexProvider
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
name|lucene
operator|.
name|util
operator|.
name|LuceneInitializerHelper
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

begin_comment
comment|/**  * same as {@link LucenePropertyFullTextTest} but will initialise a repository where the global  * full-text runs on a separate thread from lucene property.  */
end_comment

begin_class
specifier|public
class|class
name|LucenePropertyFTSeparated
extends|extends
name|LucenePropertyFullTextTest
block|{
specifier|public
name|LucenePropertyFTSeparated
parameter_list|(
specifier|final
name|File
name|dump
parameter_list|,
specifier|final
name|boolean
name|flat
parameter_list|,
specifier|final
name|boolean
name|doReport
parameter_list|,
specifier|final
name|Boolean
name|storageEnabled
parameter_list|)
block|{
name|super
argument_list|(
name|dump
argument_list|,
name|flat
argument_list|,
name|doReport
argument_list|,
name|storageEnabled
argument_list|)
expr_stmt|;
name|currentTest
operator|=
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|Repository
index|[]
name|createRepository
parameter_list|(
name|RepositoryFixture
name|fixture
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|fixture
operator|instanceof
name|OakRepositoryFixture
condition|)
block|{
name|currentFixture
operator|=
name|fixture
operator|.
name|toString
argument_list|()
expr_stmt|;
return|return
operator|(
operator|(
name|OakRepositoryFixture
operator|)
name|fixture
operator|)
operator|.
name|setUpCluster
argument_list|(
literal|1
argument_list|,
operator|new
name|JcrCreator
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Jcr
name|customize
parameter_list|(
name|Oak
name|oak
parameter_list|)
block|{
name|LuceneIndexProvider
name|provider
init|=
operator|new
name|LuceneIndexProvider
argument_list|()
decl_stmt|;
name|oak
operator|.
name|with
argument_list|(
operator|(
name|QueryIndexProvider
operator|)
name|provider
argument_list|)
operator|.
name|with
argument_list|(
operator|(
name|Observer
operator|)
name|provider
argument_list|)
operator|.
name|with
argument_list|(
operator|new
name|LuceneIndexEditorProvider
argument_list|()
argument_list|)
operator|.
name|with
argument_list|(
operator|(
operator|new
name|LuceneInitializerHelper
argument_list|(
literal|"luceneGlobal"
argument_list|,
name|storageEnabled
argument_list|)
operator|)
operator|.
name|async
argument_list|(
literal|"async-slow"
argument_list|)
argument_list|)
comment|// the WikipediaImporter set a property `title`
operator|.
name|with
argument_list|(
operator|new
name|LucenePropertyInitialiser
argument_list|(
literal|"luceneTitle"
argument_list|,
name|of
argument_list|(
literal|"title"
argument_list|)
argument_list|)
argument_list|)
operator|.
name|withAsyncIndexing
argument_list|(
literal|"async"
argument_list|,
literal|5
argument_list|)
operator|.
name|withAsyncIndexing
argument_list|(
literal|"async-slow"
argument_list|,
literal|5
argument_list|)
expr_stmt|;
return|return
operator|new
name|Jcr
argument_list|(
name|oak
argument_list|)
return|;
block|}
block|}
argument_list|)
return|;
block|}
return|return
name|super
operator|.
name|createRepository
argument_list|(
name|fixture
argument_list|)
return|;
block|}
block|}
end_class

end_unit

