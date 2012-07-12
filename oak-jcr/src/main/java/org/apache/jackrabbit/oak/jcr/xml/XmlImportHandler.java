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
name|jcr
operator|.
name|xml
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
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
name|javax
operator|.
name|jcr
operator|.
name|Node
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|RepositoryException
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
name|Attributes
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

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|helpers
operator|.
name|DefaultHandler
import|;
end_import

begin_class
specifier|public
class|class
name|XmlImportHandler
extends|extends
name|DefaultHandler
block|{
specifier|private
name|Node
name|node
decl_stmt|;
specifier|private
name|String
name|name
decl_stmt|;
specifier|private
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|values
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|StringBuilder
name|builder
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
specifier|public
name|XmlImportHandler
parameter_list|(
name|Node
name|parent
parameter_list|,
name|int
name|uuidBehavior
parameter_list|)
block|{
name|this
operator|.
name|node
operator|=
name|parent
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|startElement
parameter_list|(
name|String
name|uri
parameter_list|,
name|String
name|localName
parameter_list|,
name|String
name|qName
parameter_list|,
name|Attributes
name|atts
parameter_list|)
throws|throws
name|SAXException
block|{
if|if
condition|(
literal|"http://www.jcp.org/jcr/sv/1.0"
operator|.
name|equals
argument_list|(
name|uri
argument_list|)
condition|)
block|{
name|String
name|value
init|=
name|atts
operator|.
name|getValue
argument_list|(
literal|"sv:name"
argument_list|)
decl_stmt|;
if|if
condition|(
literal|"node"
operator|.
name|equals
argument_list|(
name|localName
argument_list|)
condition|)
block|{
try|try
block|{
name|node
operator|=
name|node
operator|.
name|addNode
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RepositoryException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|SAXException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
elseif|else
if|if
condition|(
name|value
operator|!=
literal|null
condition|)
block|{
name|name
operator|=
name|value
expr_stmt|;
block|}
name|builder
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
else|else
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|atts
operator|.
name|getLength
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
try|try
block|{
name|node
operator|.
name|setProperty
argument_list|(
name|atts
operator|.
name|getQName
argument_list|(
name|i
argument_list|)
argument_list|,
name|atts
operator|.
name|getValue
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RepositoryException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|SAXException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|endElement
parameter_list|(
name|String
name|uri
parameter_list|,
name|String
name|localName
parameter_list|,
name|String
name|qName
parameter_list|)
throws|throws
name|SAXException
block|{
if|if
condition|(
operator|!
literal|"http://www.jcp.org/jcr/sv/1.0"
operator|.
name|equals
argument_list|(
name|uri
argument_list|)
condition|)
block|{
return|return;
block|}
elseif|else
if|if
condition|(
literal|"value"
operator|.
name|equals
argument_list|(
name|localName
argument_list|)
condition|)
block|{
name|values
operator|.
name|add
argument_list|(
name|builder
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"property"
operator|.
name|equals
argument_list|(
name|localName
argument_list|)
condition|)
block|{
try|try
block|{
if|if
condition|(
name|values
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
if|if
condition|(
name|name
operator|.
name|equals
argument_list|(
literal|"jcr:primaryType"
argument_list|)
condition|)
block|{
name|node
operator|.
name|setPrimaryType
argument_list|(
name|values
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|name
operator|.
name|equals
argument_list|(
literal|"jcr:mixinTypes"
argument_list|)
condition|)
block|{
name|node
operator|.
name|addMixin
argument_list|(
name|values
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|node
operator|.
name|setProperty
argument_list|(
name|name
argument_list|,
name|values
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|name
operator|.
name|equals
argument_list|(
literal|"jcr:mixinTypes"
argument_list|)
condition|)
block|{
for|for
control|(
name|String
name|value
range|:
name|values
control|)
block|{
name|node
operator|.
name|addMixin
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|node
operator|.
name|setProperty
argument_list|(
name|name
argument_list|,
name|values
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|values
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|RepositoryException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|SAXException
argument_list|(
name|e
argument_list|)
throw|;
block|}
name|values
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|characters
parameter_list|(
name|char
index|[]
name|ch
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|)
block|{
name|builder
operator|.
name|append
argument_list|(
name|ch
argument_list|,
name|offset
argument_list|,
name|length
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

