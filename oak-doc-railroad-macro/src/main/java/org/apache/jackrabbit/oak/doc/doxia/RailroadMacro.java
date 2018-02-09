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
name|doc
operator|.
name|doxia
package|;
end_package

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
name|doc
operator|.
name|doxia
operator|.
name|jcr
operator|.
name|Railroad
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|maven
operator|.
name|doxia
operator|.
name|macro
operator|.
name|AbstractMacro
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|maven
operator|.
name|doxia
operator|.
name|macro
operator|.
name|Macro
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|maven
operator|.
name|doxia
operator|.
name|macro
operator|.
name|MacroExecutionException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|maven
operator|.
name|doxia
operator|.
name|macro
operator|.
name|MacroRequest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|maven
operator|.
name|doxia
operator|.
name|sink
operator|.
name|Sink
import|;
end_import

begin_import
import|import
name|org
operator|.
name|codehaus
operator|.
name|plexus
operator|.
name|component
operator|.
name|annotations
operator|.
name|Component
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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

begin_comment
comment|/**  * RailroadMacro macro that prints out the content of a file or a URL.  */
end_comment

begin_class
annotation|@
name|Component
argument_list|(
name|role
operator|=
name|Macro
operator|.
name|class
argument_list|,
name|hint
operator|=
literal|"railroad"
argument_list|)
specifier|public
class|class
name|RailroadMacro
extends|extends
name|AbstractMacro
block|{
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Railroad
argument_list|>
name|railroadCache
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Railroad
argument_list|>
argument_list|()
decl_stmt|;
comment|/** {@inheritDoc} */
specifier|public
name|void
name|execute
parameter_list|(
name|Sink
name|sink
parameter_list|,
name|MacroRequest
name|request
parameter_list|)
throws|throws
name|MacroExecutionException
block|{
try|try
block|{
name|String
name|fileName
init|=
operator|(
name|String
operator|)
name|request
operator|.
name|getParameter
argument_list|(
literal|"file"
argument_list|)
decl_stmt|;
name|required
argument_list|(
literal|"file"
argument_list|,
name|fileName
argument_list|)
expr_stmt|;
name|getLog
argument_list|()
operator|.
name|debug
argument_list|(
literal|"fileName: "
operator|+
name|fileName
argument_list|)
expr_stmt|;
name|String
name|topic
init|=
operator|(
name|String
operator|)
name|request
operator|.
name|getParameter
argument_list|(
literal|"topic"
argument_list|)
decl_stmt|;
name|required
argument_list|(
literal|"topic"
argument_list|,
name|topic
argument_list|)
expr_stmt|;
name|getLog
argument_list|()
operator|.
name|debug
argument_list|(
literal|"topic: "
operator|+
name|topic
argument_list|)
expr_stmt|;
name|boolean
name|setAnchor
init|=
literal|true
decl_stmt|;
name|String
name|setAnchorParam
init|=
operator|(
name|String
operator|)
name|request
operator|.
name|getParameter
argument_list|(
literal|"setAnchor"
argument_list|)
decl_stmt|;
if|if
condition|(
name|setAnchorParam
operator|!=
literal|null
operator|&&
operator|!
literal|""
operator|.
name|equals
argument_list|(
name|setAnchorParam
argument_list|)
condition|)
block|{
name|setAnchor
operator|=
name|Boolean
operator|.
name|valueOf
argument_list|(
name|setAnchorParam
argument_list|)
operator|.
name|booleanValue
argument_list|()
expr_stmt|;
block|}
name|getLog
argument_list|()
operator|.
name|debug
argument_list|(
literal|"Set Anchor: "
operator|+
name|setAnchor
argument_list|)
expr_stmt|;
name|boolean
name|renderLink
init|=
literal|false
decl_stmt|;
name|String
name|renderLinkParam
init|=
operator|(
name|String
operator|)
name|request
operator|.
name|getParameter
argument_list|(
literal|"renderLink"
argument_list|)
decl_stmt|;
if|if
condition|(
name|renderLinkParam
operator|!=
literal|null
operator|&&
operator|!
literal|""
operator|.
name|equals
argument_list|(
name|renderLinkParam
argument_list|)
condition|)
block|{
name|renderLink
operator|=
name|Boolean
operator|.
name|valueOf
argument_list|(
name|renderLinkParam
argument_list|)
operator|.
name|booleanValue
argument_list|()
expr_stmt|;
block|}
name|getLog
argument_list|()
operator|.
name|debug
argument_list|(
literal|"Render Link: "
operator|+
name|renderLink
argument_list|)
expr_stmt|;
name|Railroad
name|railroad
init|=
name|getRailroad
argument_list|(
name|fileName
argument_list|)
decl_stmt|;
if|if
condition|(
name|renderLink
condition|)
block|{
name|sink
operator|.
name|link
argument_list|(
name|railroad
operator|.
name|getLink
argument_list|(
literal|"#"
operator|+
name|topic
argument_list|)
argument_list|)
expr_stmt|;
name|sink
operator|.
name|text
argument_list|(
name|topic
argument_list|)
expr_stmt|;
name|sink
operator|.
name|link_
argument_list|()
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|setAnchor
condition|)
block|{
name|sink
operator|.
name|rawText
argument_list|(
literal|"<h2>"
argument_list|)
expr_stmt|;
name|sink
operator|.
name|anchor
argument_list|(
name|railroad
operator|.
name|getLink
argument_list|(
name|topic
argument_list|)
argument_list|)
expr_stmt|;
name|sink
operator|.
name|anchor_
argument_list|()
expr_stmt|;
name|sink
operator|.
name|text
argument_list|(
name|topic
argument_list|)
expr_stmt|;
name|sink
operator|.
name|rawText
argument_list|(
literal|"</h2>"
argument_list|)
expr_stmt|;
block|}
name|String
name|str
init|=
name|railroad
operator|.
name|render
argument_list|(
name|topic
argument_list|)
decl_stmt|;
if|if
condition|(
name|str
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|MacroExecutionException
argument_list|(
literal|"NO RAILROAD FOR "
operator|+
name|topic
operator|+
literal|" in "
operator|+
name|fileName
argument_list|)
throw|;
block|}
else|else
block|{
name|sink
operator|.
name|rawText
argument_list|(
name|str
argument_list|)
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|getLog
argument_list|()
operator|.
name|error
argument_list|(
literal|"Error creating railroad: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|MacroExecutionException
argument_list|(
literal|"Error creating railroad: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
specifier|private
name|Railroad
name|getRailroad
parameter_list|(
name|String
name|fileName
parameter_list|)
throws|throws
name|Exception
block|{
name|Railroad
name|railroad
init|=
name|railroadCache
operator|.
name|get
argument_list|(
name|fileName
argument_list|)
decl_stmt|;
if|if
condition|(
name|railroad
operator|==
literal|null
condition|)
block|{
name|getLog
argument_list|()
operator|.
name|info
argument_list|(
literal|"Creating railroad for "
operator|+
name|fileName
argument_list|)
expr_stmt|;
name|railroad
operator|=
operator|new
name|Railroad
argument_list|(
name|fileName
argument_list|)
expr_stmt|;
name|railroadCache
operator|.
name|put
argument_list|(
name|fileName
argument_list|,
name|railroad
argument_list|)
expr_stmt|;
block|}
return|return
name|railroad
return|;
block|}
block|}
end_class

end_unit

