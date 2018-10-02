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
name|search
operator|.
name|spi
operator|.
name|binary
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStream
import|;
end_import

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
name|Set
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|DocumentBuilder
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Strings
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|exception
operator|.
name|TikaException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|mime
operator|.
name|MediaType
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|parser
operator|.
name|ParseContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Element
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Node
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|NodeList
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|SAXException
import|;
end_import

begin_class
specifier|public
class|class
name|TikaParserConfig
block|{
specifier|private
specifier|static
specifier|final
name|String
name|EMPTY_PARSER
init|=
literal|"org.apache.tika.parser.EmptyParser"
decl_stmt|;
comment|/**      * Determines the set of MediaType which have been configured with an EmptyParser.      *      * @param configStream stream for tika config      * @return set of MediaTypes which are not indexed      */
specifier|public
specifier|static
name|Set
argument_list|<
name|MediaType
argument_list|>
name|getNonIndexedMediaTypes
parameter_list|(
name|InputStream
name|configStream
parameter_list|)
throws|throws
name|TikaException
throws|,
name|IOException
throws|,
name|SAXException
block|{
name|Set
argument_list|<
name|MediaType
argument_list|>
name|result
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
name|Element
name|element
init|=
name|getBuilder
argument_list|()
operator|.
name|parse
argument_list|(
name|configStream
argument_list|)
operator|.
name|getDocumentElement
argument_list|()
decl_stmt|;
name|NodeList
name|nodes
init|=
name|element
operator|.
name|getElementsByTagName
argument_list|(
literal|"parsers"
argument_list|)
decl_stmt|;
if|if
condition|(
name|nodes
operator|.
name|getLength
argument_list|()
operator|==
literal|1
condition|)
block|{
name|Node
name|parentNode
init|=
name|nodes
operator|.
name|item
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|NodeList
name|parsersNodes
init|=
name|parentNode
operator|.
name|getChildNodes
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|parsersNodes
operator|.
name|getLength
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Node
name|node
init|=
name|parsersNodes
operator|.
name|item
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|node
operator|instanceof
name|Element
condition|)
block|{
name|String
name|className
init|=
operator|(
operator|(
name|Element
operator|)
name|node
operator|)
operator|.
name|getAttribute
argument_list|(
literal|"class"
argument_list|)
decl_stmt|;
if|if
condition|(
name|EMPTY_PARSER
operator|.
name|equals
argument_list|(
name|className
argument_list|)
condition|)
block|{
name|NodeList
name|mimes
init|=
operator|(
operator|(
name|Element
operator|)
name|node
operator|)
operator|.
name|getElementsByTagName
argument_list|(
literal|"mime"
argument_list|)
decl_stmt|;
name|parseMimeTypes
argument_list|(
name|result
argument_list|,
name|mimes
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
return|return
name|result
return|;
block|}
specifier|private
specifier|static
name|void
name|parseMimeTypes
parameter_list|(
name|Set
argument_list|<
name|MediaType
argument_list|>
name|result
parameter_list|,
name|NodeList
name|mimes
parameter_list|)
block|{
comment|/*<parser class="org.apache.tika.parser.EmptyParser"><mime>application/x-archive</mime><mime>application/x-bzip</mime><mime>application/x-bzip2</mime></parser>         */
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|mimes
operator|.
name|getLength
argument_list|()
condition|;
name|j
operator|++
control|)
block|{
name|Node
name|mime
init|=
name|mimes
operator|.
name|item
argument_list|(
name|j
argument_list|)
decl_stmt|;
if|if
condition|(
name|mime
operator|instanceof
name|Element
condition|)
block|{
name|String
name|mimeValue
init|=
name|mime
operator|.
name|getTextContent
argument_list|()
decl_stmt|;
name|mimeValue
operator|=
name|Strings
operator|.
name|emptyToNull
argument_list|(
name|mimeValue
argument_list|)
expr_stmt|;
if|if
condition|(
name|mimeValue
operator|!=
literal|null
condition|)
block|{
name|MediaType
name|mediaType
init|=
name|MediaType
operator|.
name|parse
argument_list|(
name|mimeValue
operator|.
name|trim
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|mediaType
operator|!=
literal|null
condition|)
block|{
name|result
operator|.
name|add
argument_list|(
name|mediaType
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
specifier|private
specifier|static
name|DocumentBuilder
name|getBuilder
parameter_list|()
throws|throws
name|TikaException
block|{
return|return
operator|new
name|ParseContext
argument_list|()
operator|.
name|getDocumentBuilder
argument_list|()
return|;
block|}
block|}
end_class

end_unit
