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
name|jcr
operator|.
name|json
package|;
end_package

begin_comment
comment|/**  * Handler for semantic actions of a {@link JsonParser}.  * This class provides a handler which fully parses a JSON  * document by recursive decent without executing any actions.  *<p/>  * Override this class to add semantic actions as needed.   */
end_comment

begin_class
specifier|public
class|class
name|JsonHandler
block|{
comment|/**      * Default instance which can be used to skip any part of a      * JSON document.       */
specifier|public
specifier|static
specifier|final
name|JsonHandler
name|INSTANCE
init|=
operator|new
name|JsonHandler
argument_list|()
decl_stmt|;
comment|/**      * A primitive JSON value (ATOM) has been parsed.      * @param key      * @param value      */
specifier|public
name|void
name|atom
parameter_list|(
name|Token
name|key
parameter_list|,
name|Token
name|value
parameter_list|)
block|{ }
comment|/**      * A COMMA has been parsed      * @param token      */
specifier|public
name|void
name|comma
parameter_list|(
name|Token
name|token
parameter_list|)
block|{ }
comment|/**      * Parser PAIR. This implementation simply delegates back      * to {@link JsonParser#parsePair(JsonTokenizer)}      *       * @param parser      * @param tokenizer      */
specifier|public
name|void
name|pair
parameter_list|(
name|JsonParser
name|parser
parameter_list|,
name|JsonTokenizer
name|tokenizer
parameter_list|)
block|{
name|parser
operator|.
name|parsePair
argument_list|(
name|tokenizer
argument_list|)
expr_stmt|;
block|}
comment|/**      * Parser OBJECT. This implementation simply delegates back      * to {@link JsonParser#parseObject(JsonTokenizer)}      *      * @param parser      * @param key      * @param tokenizer      */
specifier|public
name|void
name|object
parameter_list|(
name|JsonParser
name|parser
parameter_list|,
name|Token
name|key
parameter_list|,
name|JsonTokenizer
name|tokenizer
parameter_list|)
block|{
name|parser
operator|.
name|parseObject
argument_list|(
name|tokenizer
argument_list|)
expr_stmt|;
block|}
comment|/**      * Parser ARRAY. This implementation simply delegates back      * to {@link JsonParser#parseArray(JsonTokenizer)}       *      * @param parser      * @param key      * @param tokenizer      */
specifier|public
name|void
name|array
parameter_list|(
name|JsonParser
name|parser
parameter_list|,
name|Token
name|key
parameter_list|,
name|JsonTokenizer
name|tokenizer
parameter_list|)
block|{
name|parser
operator|.
name|parseArray
argument_list|(
name|tokenizer
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

