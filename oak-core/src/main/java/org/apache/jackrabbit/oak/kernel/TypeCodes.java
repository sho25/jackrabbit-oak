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
name|kernel
package|;
end_package

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
operator|.
name|checkNotNull
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
name|Locale
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
name|javax
operator|.
name|jcr
operator|.
name|PropertyType
import|;
end_import

begin_comment
comment|/**  * TypeCodes maps between {@code Type} and the code used to prefix  * its json serialisation.  */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|TypeCodes
block|{
specifier|public
specifier|static
specifier|final
name|String
name|EMPTY_ARRAY
init|=
literal|"[0]:"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Map
argument_list|<
name|Integer
argument_list|,
name|String
argument_list|>
name|TYPE2CODE
init|=
operator|new
name|HashMap
argument_list|<
name|Integer
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|CODE2TYPE
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
argument_list|()
decl_stmt|;
static|static
block|{
for|for
control|(
name|int
name|type
init|=
name|PropertyType
operator|.
name|UNDEFINED
init|;
name|type
operator|<=
name|PropertyType
operator|.
name|DECIMAL
condition|;
name|type
operator|++
control|)
block|{
name|String
name|code
init|=
name|type
operator|==
name|PropertyType
operator|.
name|BINARY
condition|?
literal|":blobId"
comment|// See class comment for MicroKernel and OAK-428
else|:
name|PropertyType
operator|.
name|nameFromValue
argument_list|(
name|type
argument_list|)
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
literal|3
argument_list|)
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ENGLISH
argument_list|)
decl_stmt|;
name|TYPE2CODE
operator|.
name|put
argument_list|(
name|type
argument_list|,
name|code
argument_list|)
expr_stmt|;
name|CODE2TYPE
operator|.
name|put
argument_list|(
name|code
argument_list|,
name|type
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|TypeCodes
parameter_list|()
block|{ }
comment|/**      * Encodes the given {@code propertyName} of the given {@code propertyType} into      * a json string, which is prefixed with a type code.      * @param propertyType  type of the property      * @param propertyName  name of the property      * @return  type code prefixed json string      */
specifier|public
specifier|static
name|String
name|encode
parameter_list|(
name|int
name|propertyType
parameter_list|,
name|String
name|propertyName
parameter_list|)
block|{
name|String
name|typeCode
init|=
name|checkNotNull
argument_list|(
name|TYPE2CODE
operator|.
name|get
argument_list|(
name|propertyType
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|typeCode
operator|+
literal|':'
operator|+
name|propertyName
return|;
block|}
comment|/**      * Splits a {@code jsonString}, which is prefixed with a type code      * at the location where the prefix ends.      * @param jsonString  json string to split      * @return  the location where the prefix ends or -1 if no prefix is present      */
specifier|public
specifier|static
name|int
name|split
parameter_list|(
name|String
name|jsonString
parameter_list|)
block|{
if|if
condition|(
name|jsonString
operator|.
name|startsWith
argument_list|(
literal|":blobId:"
argument_list|)
condition|)
block|{
comment|// See OAK-428
return|return
literal|7
return|;
block|}
elseif|else
if|if
condition|(
name|jsonString
operator|.
name|length
argument_list|()
operator|>=
literal|4
operator|&&
name|jsonString
operator|.
name|charAt
argument_list|(
literal|3
argument_list|)
operator|==
literal|':'
condition|)
block|{
return|return
literal|3
return|;
block|}
else|else
block|{
return|return
operator|-
literal|1
return|;
block|}
block|}
comment|/**      * Decode the type encoded into {@code jsonString} given its split.      * @param split  split of the json string      * @param jsonString  json string      * @return  decoded type. {@code PropertyType.UNDEFINED} if none or split is not within {@code jsonString}.      */
specifier|public
specifier|static
name|int
name|decodeType
parameter_list|(
name|int
name|split
parameter_list|,
name|String
name|jsonString
parameter_list|)
block|{
if|if
condition|(
name|split
operator|==
operator|-
literal|1
operator|||
name|split
operator|>
name|jsonString
operator|.
name|length
argument_list|()
condition|)
block|{
return|return
name|PropertyType
operator|.
name|UNDEFINED
return|;
block|}
else|else
block|{
name|Integer
name|type
init|=
name|CODE2TYPE
operator|.
name|get
argument_list|(
name|jsonString
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|split
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|type
operator|==
literal|null
condition|?
name|PropertyType
operator|.
name|UNDEFINED
else|:
name|type
return|;
block|}
block|}
comment|/**      * Decode the property name encoded into a {@code jsonString} given its split.      * @param split  split of the json string      * @param jsonString  json string      * @return  decoded property name. Or {@code jsonString} if split is not with {@code jsonString}.      */
specifier|public
specifier|static
name|String
name|decodeName
parameter_list|(
name|int
name|split
parameter_list|,
name|String
name|jsonString
parameter_list|)
block|{
if|if
condition|(
name|split
operator|==
operator|-
literal|1
operator|||
name|split
operator|>=
name|jsonString
operator|.
name|length
argument_list|()
condition|)
block|{
return|return
name|jsonString
return|;
block|}
else|else
block|{
return|return
name|jsonString
operator|.
name|substring
argument_list|(
name|split
operator|+
literal|1
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

