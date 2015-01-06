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
name|api
package|;
end_package

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Collections
operator|.
name|emptyMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|ParseException
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
name|Map
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
comment|/**  * The query engine allows to parse and execute queries.  *<p>  * What query languages are supported depends on the registered query parsers.  */
end_comment

begin_interface
specifier|public
interface|interface
name|QueryEngine
block|{
comment|/**      * The suffix for internal SQL-2 statements. Those are logged with trace      * level instead of debug level.      */
name|String
name|INTERNAL_SQL2_QUERY
init|=
literal|" /* oak-internal */"
decl_stmt|;
comment|/**      * Empty set of variables bindings. Useful as an argument to      * {@link #executeQuery(String, String, long, long, Map, Map)} when      * there are no variables in a query.      */
name|Map
argument_list|<
name|String
argument_list|,
name|PropertyValue
argument_list|>
name|NO_BINDINGS
init|=
name|emptyMap
argument_list|()
decl_stmt|;
comment|/**      * Empty set of namespace prefix mappings. Useful as an argument to      * {@link #getBindVariableNames(String, String, Map)} and      * {@link #executeQuery(String, String, long, long, Map, Map)} when      * there are no local namespace mappings.      */
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|NO_MAPPINGS
init|=
name|emptyMap
argument_list|()
decl_stmt|;
comment|/**      * Get the set of supported query languages.      *      * @return the supported query languages      */
name|Set
argument_list|<
name|String
argument_list|>
name|getSupportedQueryLanguages
parameter_list|()
function_decl|;
comment|/**      * Parse the query (check if it's valid) and get the list of bind variable names.      *      * @param statement query statement      * @param language query language      * @param mappings namespace prefix mappings      * @return the list of bind variable names      * @throws ParseException      */
name|List
argument_list|<
name|String
argument_list|>
name|getBindVariableNames
parameter_list|(
name|String
name|statement
parameter_list|,
name|String
name|language
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|mappings
parameter_list|)
throws|throws
name|ParseException
function_decl|;
comment|/**      * Execute a query and get the result.      *      * @param statement the query statement      * @param language the language      * @param limit the maximum result set size (may not be negative)      * @param offset the number of rows to skip (may not be negative)      * @param bindings the bind variable value bindings      * @param mappings namespace prefix mappings      * @return the result      * @throws ParseException if the statement could not be parsed      * @throws IllegalArgumentException if there was an error executing the query      */
name|Result
name|executeQuery
parameter_list|(
name|String
name|statement
parameter_list|,
name|String
name|language
parameter_list|,
name|long
name|limit
parameter_list|,
name|long
name|offset
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|?
extends|extends
name|PropertyValue
argument_list|>
name|bindings
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|mappings
parameter_list|)
throws|throws
name|ParseException
function_decl|;
block|}
end_interface

end_unit

