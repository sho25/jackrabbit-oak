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
comment|/**  * A token represents the smallest lexical unit in a JSON document.  * A token has a {@link Type type}, a {@link #text() text} and a  * {@link #pos() position} which refers to its place in the originating  * JSON document. Note that the position is<em>not</em> taken into account  * for equality.  */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|Token
block|{
specifier|private
specifier|final
name|Type
name|type
decl_stmt|;
specifier|private
specifier|final
name|String
name|text
decl_stmt|;
specifier|private
specifier|final
name|int
name|pos
decl_stmt|;
specifier|public
enum|enum
name|Type
block|{
name|BEGIN_OBJECT
block|,
name|END_OBJECT
block|,
name|BEGIN_ARRAY
block|,
name|END_ARRAY
block|,
name|COLON
block|,
name|COMMA
block|,
name|EOF
block|,
name|TRUE
block|,
name|FALSE
block|,
name|NULL
block|,
name|STRING
block|,
name|NUMBER
block|,
name|UNKNOWN
block|}
specifier|public
name|Token
parameter_list|(
name|Type
name|type
parameter_list|,
name|String
name|text
parameter_list|,
name|int
name|pos
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
name|text
operator|=
name|text
expr_stmt|;
name|this
operator|.
name|pos
operator|=
name|pos
expr_stmt|;
block|}
specifier|public
name|Type
name|type
parameter_list|()
block|{
return|return
name|type
return|;
block|}
specifier|public
name|String
name|text
parameter_list|()
block|{
return|return
name|text
return|;
block|}
specifier|public
name|int
name|pos
parameter_list|()
block|{
return|return
name|pos
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"Token["
operator|+
name|type
operator|+
literal|", "
operator|+
name|text
operator|+
literal|", "
operator|+
name|pos
operator|+
literal|']'
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
literal|37
operator|*
operator|(
literal|37
operator|*
operator|(
literal|17
operator|+
name|type
argument_list|()
operator|.
name|hashCode
argument_list|()
operator|)
operator|+
name|text
argument_list|()
operator|.
name|hashCode
argument_list|()
operator|)
return|;
block|}
comment|/**      * Two tokens are equal if and only if their texts and their types      * are equal.      */
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|other
parameter_list|)
block|{
if|if
condition|(
name|other
operator|instanceof
name|Token
condition|)
block|{
name|Token
name|that
init|=
operator|(
name|Token
operator|)
name|other
decl_stmt|;
return|return
name|that
operator|.
name|type
operator|==
name|type
operator|&&
name|that
operator|.
name|text
operator|.
name|equals
argument_list|(
name|text
argument_list|)
return|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
block|}
end_class

end_unit

