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
name|mt
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
name|NodeState
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|joshua
operator|.
name|decoder
operator|.
name|Decoder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|joshua
operator|.
name|decoder
operator|.
name|JoshuaConfiguration
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|joshua
operator|.
name|decoder
operator|.
name|StructuredTranslation
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|joshua
operator|.
name|decoder
operator|.
name|Translation
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|joshua
operator|.
name|decoder
operator|.
name|segment_file
operator|.
name|Sentence
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
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Matchers
operator|.
name|any
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

begin_comment
comment|/**  * Tests for {@link MTFulltextQueryTermsProvider}  */
end_comment

begin_class
specifier|public
class|class
name|MTFulltextQueryTermsProviderTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|testGetQueryTermWithPhraseTranslation
parameter_list|()
throws|throws
name|Exception
block|{
name|Decoder
name|decoder
init|=
name|mock
argument_list|(
name|Decoder
operator|.
name|class
argument_list|)
decl_stmt|;
name|Translation
name|translation
init|=
name|mock
argument_list|(
name|Translation
operator|.
name|class
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|StructuredTranslation
argument_list|>
name|translations
init|=
operator|new
name|LinkedList
argument_list|<>
argument_list|()
decl_stmt|;
name|StructuredTranslation
name|structuredTranslation
init|=
name|mock
argument_list|(
name|StructuredTranslation
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|structuredTranslation
operator|.
name|getTranslationString
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|"fou bur"
argument_list|)
expr_stmt|;
name|translations
operator|.
name|add
argument_list|(
name|structuredTranslation
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|translation
operator|.
name|getStructuredTranslations
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|translations
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|decoder
operator|.
name|decode
argument_list|(
name|any
argument_list|(
name|Sentence
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|translation
argument_list|)
expr_stmt|;
name|JoshuaConfiguration
name|configuration
init|=
name|mock
argument_list|(
name|JoshuaConfiguration
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|decoder
operator|.
name|getJoshuaConfiguration
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|configuration
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|nodeTypes
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
name|MTFulltextQueryTermsProvider
name|mtFulltextQueryTermsProvider
init|=
operator|new
name|MTFulltextQueryTermsProvider
argument_list|(
name|decoder
argument_list|,
name|nodeTypes
argument_list|,
operator|-
literal|1
argument_list|)
decl_stmt|;
name|Analyzer
name|analyzer
init|=
name|mock
argument_list|(
name|Analyzer
operator|.
name|class
argument_list|)
decl_stmt|;
name|NodeState
name|indexDefinition
init|=
name|mock
argument_list|(
name|NodeState
operator|.
name|class
argument_list|)
decl_stmt|;
name|mtFulltextQueryTermsProvider
operator|.
name|getQueryTerm
argument_list|(
literal|"foo bar"
argument_list|,
name|analyzer
argument_list|,
name|indexDefinition
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

