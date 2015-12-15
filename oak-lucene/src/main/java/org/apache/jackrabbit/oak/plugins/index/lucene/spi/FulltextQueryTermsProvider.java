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

begin_comment
comment|/**  * Implementations of this interface would get callbacks while forming lucene full text queries.  */
end_comment

begin_interface
specifier|public
interface|interface
name|FulltextQueryTermsProvider
block|{
comment|/**      * This method would get called while forming full text clause for full text clause not constrained on a particular      * field.      * @param text full text term      * @param analyzer {@link Analyzer} being used while forming the query. Can be used to analyze text consistently.      * @return {@link Query} object to be OR'ed with query being prepared. {@code null}, if nothing is to be added.      */
name|Query
name|getQueryTerm
parameter_list|(
specifier|final
name|String
name|text
parameter_list|,
specifier|final
name|Analyzer
name|analyzer
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

