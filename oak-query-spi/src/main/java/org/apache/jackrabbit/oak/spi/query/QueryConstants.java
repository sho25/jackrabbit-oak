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
name|spi
operator|.
name|query
package|;
end_package

begin_class
specifier|public
specifier|abstract
class|class
name|QueryConstants
block|{
comment|/**      * Name of the property restriction used to express query performed      * via NAME and LOCALNAME functions      */
specifier|public
specifier|static
specifier|final
name|String
name|RESTRICTION_LOCAL_NAME
init|=
literal|":localname"
decl_stmt|;
comment|/**      * Name of the property restriction used to express query performed      * via NAME and LOCALNAME functions      */
specifier|public
specifier|static
specifier|final
name|String
name|RESTRICTION_NAME
init|=
literal|":name"
decl_stmt|;
comment|/**      * The prefix for restrictions for function-based indexes, for example      * upper(propertyName). Syntax: "function*expression". In order to support      * all kinds of expressions in the future (including nested expressions and      * so on), the format for the expression is written in the Polish notation      * (the RPN, reversed), with "*" as delimiter (as property names may not      * contain "*"), and "@" in front of each property name to distinguish      * between property names and functions. Literals are quoted. Examples: The      * expression "lower(lastName)" is converted to "function*lower {@literal @}      * lastName". The expression "lower(lastName)" is converted to      * "lower(upper(lastName))" is converted to "function*lower*upper*      * {@literal @}lastName". The condition      * "firstName+' '+lastName = 'Tim Cook'" would be "function*+*+ {@literal @}      * firstName*' ' {@literal @}lastName.      */
specifier|public
specifier|static
specifier|final
name|String
name|FUNCTION_RESTRICTION_PREFIX
init|=
literal|"function*"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|SEARCH_ROOT_PATH
init|=
literal|"/jcr:root"
decl_stmt|;
comment|/**      * The "jcr:path" pseudo-property.      */
comment|// TODO jcr:path isn't an official feature, support it?
specifier|public
specifier|static
specifier|final
name|String
name|JCR_PATH
init|=
literal|"jcr:path"
decl_stmt|;
comment|/**      * The "jcr:score" pseudo-property.      */
specifier|public
specifier|static
specifier|final
name|String
name|JCR_SCORE
init|=
literal|"jcr:score"
decl_stmt|;
comment|/**      * The "rep:excerpt" pseudo-property.      */
specifier|public
specifier|static
specifier|final
name|String
name|REP_EXCERPT
init|=
literal|"rep:excerpt"
decl_stmt|;
comment|/**      * The "rep:facet" pseudo-property.      */
specifier|public
specifier|static
specifier|final
name|String
name|REP_FACET
init|=
literal|"rep:facet"
decl_stmt|;
comment|/**      * The "oak:explainScore" pseudo-property.      */
specifier|public
specifier|static
specifier|final
name|String
name|OAK_SCORE_EXPLANATION
init|=
literal|"oak:scoreExplanation"
decl_stmt|;
comment|/**      * The "rep:spellcheck" pseudo-property.      */
specifier|public
specifier|static
specifier|final
name|String
name|REP_SPELLCHECK
init|=
literal|"rep:spellcheck()"
decl_stmt|;
comment|/**      * The "rep:suggest" pseudo-property.      */
specifier|public
specifier|static
specifier|final
name|String
name|REP_SUGGEST
init|=
literal|"rep:suggest()"
decl_stmt|;
block|}
end_class

end_unit

