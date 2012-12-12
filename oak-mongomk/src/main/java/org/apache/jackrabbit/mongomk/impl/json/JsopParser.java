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
name|mongomk
operator|.
name|impl
operator|.
name|json
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|SAXParser
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
name|mk
operator|.
name|json
operator|.
name|JsopReader
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
name|mk
operator|.
name|json
operator|.
name|JsopTokenizer
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
name|commons
operator|.
name|PathUtils
import|;
end_import

begin_comment
comment|/**  * An event based parser for<a href="http://wiki.apache.org/jackrabbit/Jsop">JSOP</a>.  *  *<p>  * This parser is similar to a {@link SAXParser} using a callback ({@code DefaultHandler}) to inform about certain  * events during parsing,i.e. node was added, node was removed, etc. This relieves the implementor from the burden of  * performing a semantic analysis of token which are being parsed.  *</p>  *  *<p>  * The underlying token parser is the {@link JsopTokenizer}.  *</p>  */
end_comment

begin_class
specifier|public
class|class
name|JsopParser
block|{
specifier|private
specifier|final
name|DefaultJsopHandler
name|defaultHandler
decl_stmt|;
specifier|private
specifier|final
name|String
name|path
decl_stmt|;
specifier|private
specifier|final
name|JsopTokenizer
name|tokenizer
decl_stmt|;
comment|/**      * Constructs a new {@link JsopParser}      *      * @param path The root path of the JSON diff.      * @param jsonDiff The JSON diff.      * @param defaultHandler The {@link DefaultJsopHandler} to use.      */
specifier|public
name|JsopParser
parameter_list|(
name|String
name|path
parameter_list|,
name|String
name|jsonDiff
parameter_list|,
name|DefaultJsopHandler
name|defaultHandler
parameter_list|)
block|{
name|this
operator|.
name|path
operator|=
name|path
expr_stmt|;
name|this
operator|.
name|defaultHandler
operator|=
name|defaultHandler
expr_stmt|;
name|tokenizer
operator|=
operator|new
name|JsopTokenizer
argument_list|(
name|jsonDiff
argument_list|)
expr_stmt|;
block|}
comment|/**      * Parses the JSON diff.      *      * @throws Exception If an error occurred while parsing.      */
specifier|public
name|void
name|parse
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|path
operator|.
name|length
argument_list|()
operator|>
literal|0
operator|&&
operator|!
name|PathUtils
operator|.
name|isAbsolute
argument_list|(
name|path
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Absolute path expected: "
operator|+
name|path
argument_list|)
throw|;
block|}
while|while
condition|(
literal|true
condition|)
block|{
name|int
name|token
init|=
name|tokenizer
operator|.
name|read
argument_list|()
decl_stmt|;
if|if
condition|(
name|token
operator|==
name|JsopReader
operator|.
name|END
condition|)
block|{
break|break;
block|}
switch|switch
condition|(
name|token
condition|)
block|{
case|case
literal|'+'
case|:
block|{
name|parseOpAdded
argument_list|(
name|path
argument_list|)
expr_stmt|;
break|break;
block|}
case|case
literal|'*'
case|:
block|{
name|parseOpCopied
argument_list|()
expr_stmt|;
break|break;
block|}
case|case
literal|'>'
case|:
block|{
name|parseOpMoved
argument_list|()
expr_stmt|;
break|break;
block|}
case|case
literal|'^'
case|:
block|{
name|parseOpSet
argument_list|()
expr_stmt|;
break|break;
block|}
case|case
literal|'-'
case|:
block|{
name|parseOpRemoved
argument_list|()
expr_stmt|;
break|break;
block|}
default|default:
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Illegal token '"
operator|+
operator|(
name|char
operator|)
name|token
argument_list|)
throw|;
block|}
block|}
block|}
specifier|private
name|void
name|parseOpAdded
parameter_list|(
name|String
name|currentPath
parameter_list|)
throws|throws
name|Exception
block|{
name|String
name|subPath
init|=
name|tokenizer
operator|.
name|readString
argument_list|()
decl_stmt|;
name|tokenizer
operator|.
name|read
argument_list|(
literal|':'
argument_list|)
expr_stmt|;
name|String
name|path
init|=
name|PathUtils
operator|.
name|concat
argument_list|(
name|currentPath
argument_list|,
name|subPath
argument_list|)
decl_stmt|;
if|if
condition|(
name|tokenizer
operator|.
name|matches
argument_list|(
literal|'{'
argument_list|)
condition|)
block|{
name|String
name|parentPath
init|=
name|PathUtils
operator|.
name|denotesRoot
argument_list|(
name|path
argument_list|)
condition|?
literal|""
else|:
name|PathUtils
operator|.
name|getParentPath
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|String
name|nodeName
init|=
name|PathUtils
operator|.
name|denotesRoot
argument_list|(
name|path
argument_list|)
condition|?
literal|"/"
else|:
name|PathUtils
operator|.
name|getName
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|defaultHandler
operator|.
name|nodeAdded
argument_list|(
name|parentPath
argument_list|,
name|nodeName
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|tokenizer
operator|.
name|matches
argument_list|(
literal|'}'
argument_list|)
condition|)
block|{
do|do
block|{
name|int
name|pos
init|=
name|tokenizer
operator|.
name|getLastPos
argument_list|()
decl_stmt|;
name|String
name|propName
init|=
name|tokenizer
operator|.
name|readString
argument_list|()
decl_stmt|;
name|tokenizer
operator|.
name|read
argument_list|(
literal|':'
argument_list|)
expr_stmt|;
if|if
condition|(
name|tokenizer
operator|.
name|matches
argument_list|(
literal|'{'
argument_list|)
condition|)
block|{
comment|// Nested node.
comment|// Reset to last pos as parseOpAdded expected the whole JSON.
name|tokenizer
operator|.
name|setPos
argument_list|(
name|pos
argument_list|)
expr_stmt|;
name|tokenizer
operator|.
name|read
argument_list|()
expr_stmt|;
name|parseOpAdded
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// Property.
name|String
name|valueAsString
init|=
name|tokenizer
operator|.
name|readRawValue
argument_list|()
operator|.
name|trim
argument_list|()
decl_stmt|;
name|Object
name|value
init|=
name|JsonUtil
operator|.
name|toJsonValue
argument_list|(
name|valueAsString
argument_list|)
decl_stmt|;
name|defaultHandler
operator|.
name|propertySet
argument_list|(
name|path
argument_list|,
name|propName
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
block|}
do|while
condition|(
name|tokenizer
operator|.
name|matches
argument_list|(
literal|','
argument_list|)
condition|)
do|;
name|tokenizer
operator|.
name|read
argument_list|(
literal|'}'
argument_list|)
expr_stmt|;
comment|// explicitly close the bracket
block|}
block|}
block|}
specifier|private
name|void
name|parseOpCopied
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|pos
init|=
name|tokenizer
operator|.
name|getLastPos
argument_list|()
decl_stmt|;
name|String
name|subPath
init|=
name|tokenizer
operator|.
name|readString
argument_list|()
decl_stmt|;
name|String
name|srcPath
init|=
name|PathUtils
operator|.
name|concat
argument_list|(
name|path
argument_list|,
name|subPath
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|PathUtils
operator|.
name|isAbsolute
argument_list|(
name|srcPath
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|Exception
argument_list|(
literal|"Absolute path expected: "
operator|+
name|srcPath
operator|+
literal|", pos: "
operator|+
name|pos
argument_list|)
throw|;
block|}
name|tokenizer
operator|.
name|read
argument_list|(
literal|':'
argument_list|)
expr_stmt|;
name|String
name|targetPath
init|=
name|tokenizer
operator|.
name|readString
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|PathUtils
operator|.
name|isAbsolute
argument_list|(
name|targetPath
argument_list|)
condition|)
block|{
name|targetPath
operator|=
name|PathUtils
operator|.
name|concat
argument_list|(
name|path
argument_list|,
name|targetPath
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|PathUtils
operator|.
name|isAbsolute
argument_list|(
name|targetPath
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|Exception
argument_list|(
literal|"Absolute path expected: "
operator|+
name|targetPath
operator|+
literal|", pos: "
operator|+
name|pos
argument_list|)
throw|;
block|}
block|}
name|defaultHandler
operator|.
name|nodeCopied
argument_list|(
name|path
argument_list|,
name|srcPath
argument_list|,
name|targetPath
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|parseOpMoved
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|pos
init|=
name|tokenizer
operator|.
name|getLastPos
argument_list|()
decl_stmt|;
name|String
name|subPath
init|=
name|tokenizer
operator|.
name|readString
argument_list|()
decl_stmt|;
name|String
name|srcPath
init|=
name|PathUtils
operator|.
name|concat
argument_list|(
name|path
argument_list|,
name|subPath
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|PathUtils
operator|.
name|isAbsolute
argument_list|(
name|srcPath
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|Exception
argument_list|(
literal|"Absolute path expected: "
operator|+
name|srcPath
operator|+
literal|", pos: "
operator|+
name|pos
argument_list|)
throw|;
block|}
name|tokenizer
operator|.
name|read
argument_list|(
literal|':'
argument_list|)
expr_stmt|;
name|pos
operator|=
name|tokenizer
operator|.
name|getLastPos
argument_list|()
expr_stmt|;
name|String
name|targetPath
init|=
name|tokenizer
operator|.
name|readString
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|PathUtils
operator|.
name|isAbsolute
argument_list|(
name|targetPath
argument_list|)
condition|)
block|{
name|targetPath
operator|=
name|PathUtils
operator|.
name|concat
argument_list|(
name|path
argument_list|,
name|targetPath
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|PathUtils
operator|.
name|isAbsolute
argument_list|(
name|targetPath
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|Exception
argument_list|(
literal|"absolute path expected: "
operator|+
name|targetPath
operator|+
literal|", pos: "
operator|+
name|pos
argument_list|)
throw|;
block|}
block|}
name|defaultHandler
operator|.
name|nodeMoved
argument_list|(
name|path
argument_list|,
name|srcPath
argument_list|,
name|targetPath
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|parseOpSet
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|pos
init|=
name|tokenizer
operator|.
name|getLastPos
argument_list|()
decl_stmt|;
name|String
name|subPath
init|=
name|tokenizer
operator|.
name|readString
argument_list|()
decl_stmt|;
name|tokenizer
operator|.
name|read
argument_list|(
literal|':'
argument_list|)
expr_stmt|;
name|String
name|value
decl_stmt|;
if|if
condition|(
name|tokenizer
operator|.
name|matches
argument_list|(
name|JsopReader
operator|.
name|NULL
argument_list|)
condition|)
block|{
name|value
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
name|value
operator|=
name|tokenizer
operator|.
name|readRawValue
argument_list|()
operator|.
name|trim
argument_list|()
expr_stmt|;
block|}
name|String
name|targetPath
init|=
name|PathUtils
operator|.
name|concat
argument_list|(
name|path
argument_list|,
name|subPath
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|PathUtils
operator|.
name|isAbsolute
argument_list|(
name|targetPath
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|Exception
argument_list|(
literal|"Absolute path expected: "
operator|+
name|targetPath
operator|+
literal|", pos: "
operator|+
name|pos
argument_list|)
throw|;
block|}
name|String
name|parentPath
init|=
name|PathUtils
operator|.
name|getParentPath
argument_list|(
name|targetPath
argument_list|)
decl_stmt|;
name|String
name|propName
init|=
name|PathUtils
operator|.
name|getName
argument_list|(
name|targetPath
argument_list|)
decl_stmt|;
name|defaultHandler
operator|.
name|propertySet
argument_list|(
name|parentPath
argument_list|,
name|propName
argument_list|,
name|JsonUtil
operator|.
name|toJsonValue
argument_list|(
name|value
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|parseOpRemoved
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|pos
init|=
name|tokenizer
operator|.
name|getLastPos
argument_list|()
decl_stmt|;
name|String
name|subPath
init|=
name|tokenizer
operator|.
name|readString
argument_list|()
decl_stmt|;
name|String
name|targetPath
init|=
name|PathUtils
operator|.
name|concat
argument_list|(
name|path
argument_list|,
name|subPath
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|PathUtils
operator|.
name|isAbsolute
argument_list|(
name|targetPath
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|Exception
argument_list|(
literal|"Absolute path expected: "
operator|+
name|targetPath
operator|+
literal|", pos: "
operator|+
name|pos
argument_list|)
throw|;
block|}
name|defaultHandler
operator|.
name|nodeRemoved
argument_list|(
name|path
argument_list|,
name|subPath
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

