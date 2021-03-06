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
name|io
operator|.
name|StringReader
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
name|plugins
operator|.
name|index
operator|.
name|lucene
operator|.
name|OakAnalyzer
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
name|spi
operator|.
name|FulltextQueryTermsProvider
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
name|search
operator|.
name|FieldNames
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
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|TokenStream
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
name|tokenattributes
operator|.
name|CharTermAttribute
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
name|Term
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
name|queryparser
operator|.
name|simple
operator|.
name|SimpleQueryParser
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
name|BooleanClause
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
name|BooleanQuery
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
name|Query
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
name|TermQuery
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
name|Version
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
name|NotNull
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_comment
comment|/**  * {@link FulltextQueryTermsProvider} that performs machine translation on full text returning a query containing  * translated tokens.  */
end_comment

begin_class
specifier|public
class|class
name|MTFulltextQueryTermsProvider
implements|implements
name|FulltextQueryTermsProvider
block|{
specifier|private
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|getClass
argument_list|()
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|Decoder
name|decoder
decl_stmt|;
specifier|private
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|nodeTypes
decl_stmt|;
specifier|private
specifier|final
name|float
name|minScore
decl_stmt|;
specifier|private
specifier|final
name|SimpleQueryParser
name|qp
decl_stmt|;
specifier|public
name|MTFulltextQueryTermsProvider
parameter_list|(
name|Decoder
name|decoder
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|nodeTypes
parameter_list|,
name|float
name|minScore
parameter_list|)
block|{
name|this
operator|.
name|decoder
operator|=
name|decoder
expr_stmt|;
name|this
operator|.
name|nodeTypes
operator|=
name|nodeTypes
expr_stmt|;
name|this
operator|.
name|minScore
operator|=
name|minScore
expr_stmt|;
name|this
operator|.
name|qp
operator|=
operator|new
name|SimpleQueryParser
argument_list|(
operator|new
name|OakAnalyzer
argument_list|(
name|Version
operator|.
name|LUCENE_47
argument_list|)
argument_list|,
name|FieldNames
operator|.
name|FULLTEXT
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Query
name|getQueryTerm
parameter_list|(
name|String
name|text
parameter_list|,
name|Analyzer
name|analyzer
parameter_list|,
name|NodeState
name|indexDefinition
parameter_list|)
block|{
name|BooleanQuery
name|query
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
try|try
block|{
name|Sentence
name|sentence
init|=
operator|new
name|Sentence
argument_list|(
name|text
argument_list|,
name|text
operator|.
name|hashCode
argument_list|()
argument_list|,
name|decoder
operator|.
name|getJoshuaConfiguration
argument_list|()
argument_list|)
decl_stmt|;
name|Translation
name|translation
init|=
name|decoder
operator|.
name|decode
argument_list|(
name|sentence
argument_list|)
decl_stmt|;
name|log
operator|.
name|debug
argument_list|(
literal|"{} decoded into {}"
argument_list|,
name|text
argument_list|,
name|translation
argument_list|)
expr_stmt|;
name|query
operator|.
name|add
argument_list|(
operator|new
name|BooleanClause
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|FieldNames
operator|.
name|FULLTEXT
argument_list|,
name|translation
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
argument_list|)
expr_stmt|;
comment|// try phrase translation first
name|List
argument_list|<
name|StructuredTranslation
argument_list|>
name|structuredTranslations
init|=
name|translation
operator|.
name|getStructuredTranslations
argument_list|()
decl_stmt|;
name|log
operator|.
name|debug
argument_list|(
literal|"found {} structured translations"
argument_list|,
name|structuredTranslations
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|structuredTranslations
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"phrase translation"
argument_list|)
expr_stmt|;
name|addTranslations
argument_list|(
name|query
argument_list|,
name|structuredTranslations
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// if phrase cannot be translated, perform token by token translation
name|log
operator|.
name|debug
argument_list|(
literal|"per token translation"
argument_list|)
expr_stmt|;
name|TokenStream
name|tokenStream
init|=
name|analyzer
operator|.
name|tokenStream
argument_list|(
literal|null
argument_list|,
operator|new
name|StringReader
argument_list|(
name|text
argument_list|)
argument_list|)
decl_stmt|;
name|tokenStream
operator|.
name|addAttribute
argument_list|(
name|CharTermAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
name|tokenStream
operator|.
name|reset
argument_list|()
expr_stmt|;
while|while
condition|(
name|tokenStream
operator|.
name|incrementToken
argument_list|()
condition|)
block|{
name|CharTermAttribute
name|attribute
init|=
name|tokenStream
operator|.
name|getAttribute
argument_list|(
name|CharTermAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|String
name|source
init|=
name|attribute
operator|.
name|toString
argument_list|()
decl_stmt|;
name|Translation
name|translatedToken
init|=
name|decoder
operator|.
name|decode
argument_list|(
operator|new
name|Sentence
argument_list|(
name|source
argument_list|,
name|source
operator|.
name|hashCode
argument_list|()
argument_list|,
name|decoder
operator|.
name|getJoshuaConfiguration
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|addTranslations
argument_list|(
name|query
argument_list|,
name|translatedToken
operator|.
name|getStructuredTranslations
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|tokenStream
operator|.
name|end
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"could not translate query"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
return|return
name|query
operator|.
name|clauses
argument_list|()
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|?
name|query
else|:
literal|null
return|;
block|}
specifier|private
name|void
name|addTranslations
parameter_list|(
name|BooleanQuery
name|query
parameter_list|,
name|List
argument_list|<
name|StructuredTranslation
argument_list|>
name|structuredTranslations
parameter_list|)
block|{
for|for
control|(
name|StructuredTranslation
name|st
range|:
name|structuredTranslations
control|)
block|{
name|String
name|translationString
init|=
name|st
operator|.
name|getTranslationString
argument_list|()
decl_stmt|;
name|float
name|translationScore
init|=
name|st
operator|.
name|getTranslationScore
argument_list|()
decl_stmt|;
name|log
operator|.
name|debug
argument_list|(
literal|"translation {} has score {}"
argument_list|,
name|translationString
argument_list|,
name|translationScore
argument_list|)
expr_stmt|;
if|if
condition|(
name|translationScore
operator|>
name|minScore
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"translation score for {} is {}"
argument_list|,
name|translationString
argument_list|,
name|translationScore
argument_list|)
expr_stmt|;
name|query
operator|.
name|add
argument_list|(
operator|new
name|BooleanClause
argument_list|(
name|qp
operator|.
name|createPhraseQuery
argument_list|(
name|FieldNames
operator|.
name|FULLTEXT
argument_list|,
name|translationString
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
argument_list|)
expr_stmt|;
name|log
operator|.
name|debug
argument_list|(
literal|"added query for translated phrase {}"
argument_list|,
name|translationString
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|translationTokens
init|=
name|st
operator|.
name|getTranslationTokens
argument_list|()
decl_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
comment|// if output is a phrase, look for tokens having a word alignment to the original sentence terms
for|for
control|(
name|List
argument_list|<
name|Integer
argument_list|>
name|wa
range|:
name|st
operator|.
name|getTranslationWordAlignments
argument_list|()
control|)
block|{
if|if
condition|(
operator|!
name|wa
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|String
name|translatedTerm
init|=
name|translationTokens
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|Query
name|termQuery
init|=
name|qp
operator|.
name|parse
argument_list|(
name|translatedTerm
argument_list|)
decl_stmt|;
name|query
operator|.
name|add
argument_list|(
operator|new
name|BooleanClause
argument_list|(
name|termQuery
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
argument_list|)
expr_stmt|;
name|log
operator|.
name|debug
argument_list|(
literal|"added query for translated token {}"
argument_list|,
name|translatedTerm
argument_list|)
expr_stmt|;
block|}
name|i
operator|++
expr_stmt|;
block|}
block|}
block|}
block|}
specifier|public
name|void
name|clearResources
parameter_list|()
block|{
name|decoder
operator|.
name|cleanUp
argument_list|()
expr_stmt|;
block|}
annotation|@
name|NotNull
annotation|@
name|Override
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|getSupportedTypes
parameter_list|()
block|{
return|return
name|nodeTypes
return|;
block|}
block|}
end_class

end_unit

