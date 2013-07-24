begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements. See the NOTICE file distributed with this  * work for additional information regarding copyright ownership. The ASF  * licenses this file to You under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law  * or agreed to in writing, software distributed under the License is  * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied. See the License for the specific language  * governing permissions and limitations under the License.  */
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
name|query
package|;
end_package

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
name|Set
import|;
end_import

begin_comment
comment|/**  * Query parser interface.  *  * TODO: move to o.a.j.o.spi (figure out what to do with the Query class)  */
end_comment

begin_interface
specifier|public
interface|interface
name|QueryParser
block|{
comment|/**      * Returns the set of query languages supported by this parser.      *      * @return supported query languages      */
name|Set
argument_list|<
name|String
argument_list|>
name|getSupportedLanguages
parameter_list|()
function_decl|;
comment|/**      * Parses the given query string, expressed in the specified language.      *      * TODO: Include name mapping information      *      * @param query query string      * @param language query language      * @return parsed query      * @throws ParseException if the query string could not be parsed      */
name|Query
name|parse
parameter_list|(
name|String
name|query
parameter_list|,
name|String
name|language
parameter_list|)
throws|throws
name|ParseException
function_decl|;
block|}
end_interface

end_unit

