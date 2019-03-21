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
name|http
package|;
end_package

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
name|mime
operator|.
name|MediaTypeRegistry
import|;
end_import

begin_class
specifier|public
class|class
name|MediaRange
block|{
specifier|private
specifier|final
name|MediaType
name|type
decl_stmt|;
specifier|private
specifier|final
name|double
name|q
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|QRESOLUTION
init|=
literal|1000
decl_stmt|;
specifier|private
specifier|static
name|double
name|CORRECTIONFORSUBTYPEMATCH
init|=
literal|1f
operator|/
operator|(
literal|10
operator|*
name|QRESOLUTION
operator|)
decl_stmt|;
specifier|private
specifier|static
name|double
name|CORRECTIONFORTYPEANDSUBTYPEMATCH
init|=
literal|2f
operator|/
operator|(
literal|10
operator|*
name|QRESOLUTION
operator|)
decl_stmt|;
specifier|public
specifier|static
name|MediaRange
name|parse
parameter_list|(
name|String
name|range
parameter_list|,
name|MediaTypeRegistry
name|registry
parameter_list|)
block|{
name|MediaType
name|type
init|=
name|MediaType
operator|.
name|parse
argument_list|(
name|range
argument_list|)
decl_stmt|;
if|if
condition|(
name|type
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|type
operator|=
name|registry
operator|.
name|normalize
argument_list|(
name|type
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|parameters
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|(
name|type
operator|.
name|getParameters
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|q
init|=
name|parameters
operator|.
name|remove
argument_list|(
literal|"q"
argument_list|)
decl_stmt|;
if|if
condition|(
name|q
operator|!=
literal|null
condition|)
block|{
try|try
block|{
return|return
operator|new
name|MediaRange
argument_list|(
operator|new
name|MediaType
argument_list|(
name|type
operator|.
name|getBaseType
argument_list|()
argument_list|,
name|parameters
argument_list|)
argument_list|,
name|Double
operator|.
name|parseDouble
argument_list|(
name|q
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
block|}
return|return
operator|new
name|MediaRange
argument_list|(
name|type
argument_list|,
literal|1.0
argument_list|)
return|;
block|}
specifier|public
name|MediaRange
parameter_list|(
name|MediaType
name|type
parameter_list|,
name|double
name|q
parameter_list|)
block|{
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
name|this
operator|.
name|q
operator|=
name|q
expr_stmt|;
block|}
comment|/**      * Matches the media range against the specified media type.      *<p>      * The "derived" quality value if computed from the specified q value (0 to      * 1) by subtracting 1/10000 in case the subtype in the range is "*", or      * 2/10000 if case both type and subtype are. This takes care of the      * precedence specified in RFC 7231, Section 5.3.2.      *      * @param type      *            type to match      * @param registry      *            media type registry      * @return {@code 0.0} for "no match", the derived quality value if match      */
specifier|public
name|double
name|match
parameter_list|(
name|MediaType
name|type
parameter_list|,
name|MediaTypeRegistry
name|registry
parameter_list|)
block|{
if|if
condition|(
name|type
operator|.
name|equals
argument_list|(
name|this
operator|.
name|type
argument_list|)
condition|)
block|{
comment|// shortcut
return|return
name|q
return|;
block|}
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|entry
range|:
name|this
operator|.
name|type
operator|.
name|getParameters
argument_list|()
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
name|key
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|String
name|value
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|value
operator|.
name|equals
argument_list|(
name|type
operator|.
name|getParameters
argument_list|()
operator|.
name|get
argument_list|(
name|key
argument_list|)
argument_list|)
condition|)
block|{
return|return
literal|0.0
return|;
block|}
block|}
if|if
condition|(
name|q
operator|>
literal|0.0
operator|&&
literal|"*/*"
operator|.
name|equals
argument_list|(
name|this
operator|.
name|type
operator|.
name|toString
argument_list|()
argument_list|)
condition|)
block|{
return|return
name|q
operator|-
name|CORRECTIONFORTYPEANDSUBTYPEMATCH
return|;
block|}
elseif|else
if|if
condition|(
name|q
operator|>
literal|0.0
operator|&&
literal|"*"
operator|.
name|equals
argument_list|(
name|this
operator|.
name|type
operator|.
name|getSubtype
argument_list|()
argument_list|)
operator|&&
name|type
operator|.
name|getType
argument_list|()
operator|.
name|equals
argument_list|(
name|this
operator|.
name|type
operator|.
name|getType
argument_list|()
argument_list|)
condition|)
block|{
return|return
name|q
operator|-
name|CORRECTIONFORSUBTYPEMATCH
return|;
block|}
else|else
block|{
return|return
literal|0.0
return|;
block|}
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|String
operator|.
name|format
argument_list|(
literal|"type=%s, q=%f"
argument_list|,
name|type
argument_list|,
name|q
argument_list|)
return|;
block|}
block|}
end_class

end_unit

