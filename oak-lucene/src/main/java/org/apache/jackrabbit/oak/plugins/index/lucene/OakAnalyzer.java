begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|io
operator|.
name|Reader
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
name|core
operator|.
name|LowerCaseFilter
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
name|miscellaneous
operator|.
name|WordDelimiterFilter
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
name|standard
operator|.
name|StandardTokenizer
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

begin_comment
comment|/**  * The default Lucene Analyzer used in Oak.  */
end_comment

begin_class
specifier|public
class|class
name|OakAnalyzer
extends|extends
name|Analyzer
block|{
specifier|private
specifier|final
name|Version
name|matchVersion
decl_stmt|;
specifier|private
specifier|final
name|int
name|INDEX_ORIGINAL_TERM
decl_stmt|;
comment|/**      * Creates a new {@link OakAnalyzer}      *      * @param matchVersion      *            Lucene version to match See      *            {@link #matchVersion above}      */
specifier|public
name|OakAnalyzer
parameter_list|(
name|Version
name|matchVersion
parameter_list|)
block|{
name|this
argument_list|(
name|matchVersion
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/**      * Create a new {@link OakAnalyzer} with configurable flag to preserve      * original term being analyzed too.      * @param matchVersion Lucene version to match See {@link #matchVersion above}      * @param indexOriginalTerm flag to setup analyzer such that      *                              {@link WordDelimiterFilter#PRESERVE_ORIGINAL}      *                              is set to oonfigure word delimeter      */
specifier|public
name|OakAnalyzer
parameter_list|(
name|Version
name|matchVersion
parameter_list|,
name|boolean
name|indexOriginalTerm
parameter_list|)
block|{
name|this
operator|.
name|matchVersion
operator|=
name|matchVersion
expr_stmt|;
name|INDEX_ORIGINAL_TERM
operator|=
name|indexOriginalTerm
condition|?
name|WordDelimiterFilter
operator|.
name|PRESERVE_ORIGINAL
else|:
literal|0
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|TokenStreamComponents
name|createComponents
parameter_list|(
specifier|final
name|String
name|fieldName
parameter_list|,
specifier|final
name|Reader
name|reader
parameter_list|)
block|{
name|StandardTokenizer
name|src
init|=
operator|new
name|StandardTokenizer
argument_list|(
name|matchVersion
argument_list|,
name|reader
argument_list|)
decl_stmt|;
name|TokenStream
name|tok
init|=
operator|new
name|LowerCaseFilter
argument_list|(
name|matchVersion
argument_list|,
name|src
argument_list|)
decl_stmt|;
name|tok
operator|=
operator|new
name|WordDelimiterFilter
argument_list|(
name|tok
argument_list|,
name|WordDelimiterFilter
operator|.
name|GENERATE_WORD_PARTS
operator||
name|WordDelimiterFilter
operator|.
name|STEM_ENGLISH_POSSESSIVE
operator||
name|this
operator|.
name|INDEX_ORIGINAL_TERM
operator||
name|WordDelimiterFilter
operator|.
name|GENERATE_NUMBER_PARTS
argument_list|,
literal|null
argument_list|)
expr_stmt|;
return|return
operator|new
name|TokenStreamComponents
argument_list|(
name|src
argument_list|,
name|tok
argument_list|)
return|;
block|}
block|}
end_class

end_unit

