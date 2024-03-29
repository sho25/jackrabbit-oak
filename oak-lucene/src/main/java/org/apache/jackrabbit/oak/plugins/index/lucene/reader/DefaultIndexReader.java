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
operator|.
name|reader
package|;
end_package

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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|io
operator|.
name|Closer
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
name|SuggestHelper
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
name|analysis
operator|.
name|Analyzer
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
name|DirectoryReader
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|suggest
operator|.
name|analyzing
operator|.
name|AnalyzingInfixSuggester
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
name|store
operator|.
name|Directory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jetbrains
operator|.
name|annotations
operator|.
name|Nullable
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
name|plugins
operator|.
name|index
operator|.
name|lucene
operator|.
name|directory
operator|.
name|DirectoryUtils
operator|.
name|dirSize
import|;
end_import

begin_class
specifier|public
class|class
name|DefaultIndexReader
implements|implements
name|LuceneIndexReader
block|{
specifier|private
specifier|final
name|Closer
name|closer
decl_stmt|;
specifier|private
specifier|final
name|Directory
name|directory
decl_stmt|;
specifier|private
specifier|final
name|Directory
name|suggestDirectory
decl_stmt|;
specifier|private
specifier|final
name|IndexReader
name|reader
decl_stmt|;
specifier|private
specifier|final
name|AnalyzingInfixSuggester
name|lookup
decl_stmt|;
specifier|public
name|DefaultIndexReader
parameter_list|(
name|Directory
name|directory
parameter_list|,
annotation|@
name|Nullable
name|Directory
name|suggestDirectory
parameter_list|,
name|Analyzer
name|analyzer
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|closer
operator|=
name|Closer
operator|.
name|create
argument_list|()
expr_stmt|;
name|this
operator|.
name|directory
operator|=
name|directory
expr_stmt|;
name|closer
operator|.
name|register
argument_list|(
name|this
operator|.
name|directory
argument_list|)
expr_stmt|;
name|this
operator|.
name|reader
operator|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|directory
argument_list|)
expr_stmt|;
name|closer
operator|.
name|register
argument_list|(
name|this
operator|.
name|reader
argument_list|)
expr_stmt|;
name|this
operator|.
name|suggestDirectory
operator|=
name|suggestDirectory
expr_stmt|;
if|if
condition|(
name|suggestDirectory
operator|!=
literal|null
condition|)
block|{
comment|//Directory is closed by AnalyzingInfixSuggester close call
name|this
operator|.
name|lookup
operator|=
name|SuggestHelper
operator|.
name|getLookup
argument_list|(
name|suggestDirectory
argument_list|,
name|analyzer
argument_list|)
expr_stmt|;
name|closer
operator|.
name|register
argument_list|(
name|this
operator|.
name|lookup
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|lookup
operator|=
literal|null
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|IndexReader
name|getReader
parameter_list|()
block|{
return|return
name|reader
return|;
block|}
annotation|@
name|Override
annotation|@
name|Nullable
specifier|public
name|AnalyzingInfixSuggester
name|getLookup
parameter_list|()
block|{
return|return
name|lookup
return|;
block|}
annotation|@
name|Override
annotation|@
name|Nullable
specifier|public
name|Directory
name|getSuggestDirectory
parameter_list|()
block|{
return|return
name|suggestDirectory
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getIndexSize
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|dirSize
argument_list|(
name|directory
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|closer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

