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
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|io
operator|.
name|FileUtils
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
name|Blob
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
name|fulltext
operator|.
name|ExtractedText
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
name|memory
operator|.
name|ArrayBasedBlob
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

begin_class
specifier|public
class|class
name|ExtractedTextCacheTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|cacheDisabling
parameter_list|()
throws|throws
name|Exception
block|{
name|ExtractedTextCache
name|cache
init|=
operator|new
name|ExtractedTextCache
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
name|cache
operator|.
name|getCacheStats
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|cacheEnabled
parameter_list|()
throws|throws
name|Exception
block|{
name|ExtractedTextCache
name|cache
init|=
operator|new
name|ExtractedTextCache
argument_list|(
literal|10
operator|*
name|FileUtils
operator|.
name|ONE_MB
argument_list|,
literal|100
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|cache
operator|.
name|getCacheStats
argument_list|()
argument_list|)
expr_stmt|;
name|Blob
name|b
init|=
operator|new
name|IdBlob
argument_list|(
literal|"hello"
argument_list|,
literal|"a"
argument_list|)
decl_stmt|;
name|String
name|text
init|=
name|cache
operator|.
name|get
argument_list|(
literal|"/a"
argument_list|,
literal|"foo"
argument_list|,
name|b
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
name|text
argument_list|)
expr_stmt|;
name|cache
operator|.
name|put
argument_list|(
name|b
argument_list|,
operator|new
name|ExtractedText
argument_list|(
name|ExtractedText
operator|.
name|ExtractionResult
operator|.
name|SUCCESS
argument_list|,
literal|"test hello"
argument_list|)
argument_list|)
expr_stmt|;
name|text
operator|=
name|cache
operator|.
name|get
argument_list|(
literal|"/a"
argument_list|,
literal|"foo"
argument_list|,
name|b
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"test hello"
argument_list|,
name|text
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|cacheEnabledNonIdBlob
parameter_list|()
throws|throws
name|Exception
block|{
name|ExtractedTextCache
name|cache
init|=
operator|new
name|ExtractedTextCache
argument_list|(
literal|10
operator|*
name|FileUtils
operator|.
name|ONE_MB
argument_list|,
literal|100
argument_list|)
decl_stmt|;
name|Blob
name|b
init|=
operator|new
name|ArrayBasedBlob
argument_list|(
literal|"hello"
operator|.
name|getBytes
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|text
init|=
name|cache
operator|.
name|get
argument_list|(
literal|"/a"
argument_list|,
literal|"foo"
argument_list|,
name|b
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
name|text
argument_list|)
expr_stmt|;
name|cache
operator|.
name|put
argument_list|(
name|b
argument_list|,
operator|new
name|ExtractedText
argument_list|(
name|ExtractedText
operator|.
name|ExtractionResult
operator|.
name|SUCCESS
argument_list|,
literal|"test hello"
argument_list|)
argument_list|)
expr_stmt|;
name|text
operator|=
name|cache
operator|.
name|get
argument_list|(
literal|"/a"
argument_list|,
literal|"foo"
argument_list|,
name|b
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|text
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|cacheEnabledErrorInTextExtraction
parameter_list|()
throws|throws
name|Exception
block|{
name|ExtractedTextCache
name|cache
init|=
operator|new
name|ExtractedTextCache
argument_list|(
literal|10
operator|*
name|FileUtils
operator|.
name|ONE_MB
argument_list|,
literal|100
argument_list|)
decl_stmt|;
name|Blob
name|b
init|=
operator|new
name|IdBlob
argument_list|(
literal|"hello"
argument_list|,
literal|"a"
argument_list|)
decl_stmt|;
name|String
name|text
init|=
name|cache
operator|.
name|get
argument_list|(
literal|"/a"
argument_list|,
literal|"foo"
argument_list|,
name|b
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
name|text
argument_list|)
expr_stmt|;
name|cache
operator|.
name|put
argument_list|(
name|b
argument_list|,
operator|new
name|ExtractedText
argument_list|(
name|ExtractedText
operator|.
name|ExtractionResult
operator|.
name|ERROR
argument_list|,
literal|"test hello"
argument_list|)
argument_list|)
expr_stmt|;
name|text
operator|=
name|cache
operator|.
name|get
argument_list|(
literal|"/a"
argument_list|,
literal|"foo"
argument_list|,
name|b
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|text
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
class|class
name|IdBlob
extends|extends
name|ArrayBasedBlob
block|{
specifier|final
name|String
name|id
decl_stmt|;
specifier|public
name|IdBlob
parameter_list|(
name|String
name|value
parameter_list|,
name|String
name|id
parameter_list|)
block|{
name|super
argument_list|(
name|value
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getContentIdentity
parameter_list|()
block|{
return|return
name|id
return|;
block|}
block|}
block|}
end_class

end_unit

