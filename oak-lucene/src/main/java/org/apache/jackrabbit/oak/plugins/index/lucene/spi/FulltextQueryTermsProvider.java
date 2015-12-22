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
operator|.
name|spi
package|;
end_package

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
name|search
operator|.
name|Query
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nonnull
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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

begin_comment
comment|/**  * Implementations of this interface would get callbacks while forming lucene full text queries.  */
end_comment

begin_interface
specifier|public
interface|interface
name|FulltextQueryTermsProvider
block|{
comment|/**      * Implementation which doesn't do anything useful... yet, abides with the contract.      */
name|FulltextQueryTermsProvider
name|DEFAULT
init|=
operator|new
name|FulltextQueryTermsProvider
argument_list|()
block|{
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
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
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
name|Collections
operator|.
name|EMPTY_SET
return|;
block|}
block|}
decl_stmt|;
comment|/**      * This method would get called while forming full text clause for full text clause not constrained on a particular      * field.      * @param text full text term      * @param analyzer {@link Analyzer} being used while forming the query. Can be used to analyze text consistently.      * @return {@link Query} object to be OR'ed with query being prepared. {@code null}, if nothing is to be added.      */
name|Query
name|getQueryTerm
parameter_list|(
name|String
name|text
parameter_list|,
name|Analyzer
name|analyzer
parameter_list|)
function_decl|;
comment|/**      * This method is used to find which node types are supported by the implementation. Based, on the index      * definition being used to query the document, only those implementations would get callback to      * {@link FulltextQueryTermsProvider#getQueryTerm} which declare a matching node type. Note, node types are      * exact matches and do not support inheritance.      * @return {@link Set} of types supported by the implementation      */
annotation|@
name|Nonnull
name|Set
argument_list|<
name|String
argument_list|>
name|getSupportedTypes
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

