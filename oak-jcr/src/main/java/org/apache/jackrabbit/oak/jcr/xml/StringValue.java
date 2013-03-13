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
name|javax
operator|.
name|jcr
operator|.
name|PropertyType
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
name|javax
operator|.
name|jcr
operator|.
name|Value
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|ValueFactory
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
name|namepath
operator|.
name|NamePathMapper
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
name|spi
operator|.
name|xml
operator|.
name|TextValue
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
name|value
operator|.
name|ValueHelper
import|;
end_import

begin_comment
comment|/**  * {@code StringValue} represents an immutable serialized value.  */
end_comment

begin_class
class|class
name|StringValue
implements|implements
name|TextValue
block|{
specifier|private
specifier|final
name|String
name|value
decl_stmt|;
specifier|private
specifier|final
name|ValueFactory
name|valueFactory
decl_stmt|;
specifier|private
specifier|final
name|NamePathMapper
name|namePathMapper
decl_stmt|;
comment|/**      * Constructs a new {@code StringValue} representing the given      * value.      *      * @param value serialized value from document      * @param valueFactory the ValueFactory      * @param namePathMapper a namePathMapper knowing the document context      */
specifier|protected
name|StringValue
parameter_list|(
name|String
name|value
parameter_list|,
name|ValueFactory
name|valueFactory
parameter_list|,
name|NamePathMapper
name|namePathMapper
parameter_list|)
block|{
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
name|this
operator|.
name|valueFactory
operator|=
name|valueFactory
expr_stmt|;
name|this
operator|.
name|namePathMapper
operator|=
name|namePathMapper
expr_stmt|;
block|}
comment|//--------------------------------------------------------< TextValue>
annotation|@
name|Override
specifier|public
name|String
name|getString
parameter_list|()
block|{
return|return
name|this
operator|.
name|value
return|;
block|}
annotation|@
name|Override
specifier|public
name|Value
name|getValue
parameter_list|(
name|int
name|type
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|String
name|inputValue
init|=
name|type
operator|==
name|PropertyType
operator|.
name|NAME
condition|?
name|namePathMapper
operator|.
name|getOakName
argument_list|(
name|value
argument_list|)
else|:
name|type
operator|==
name|PropertyType
operator|.
name|PATH
condition|?
name|namePathMapper
operator|.
name|getOakPath
argument_list|(
name|value
argument_list|)
else|:
name|value
decl_stmt|;
return|return
name|ValueHelper
operator|.
name|deserialize
argument_list|(
name|inputValue
argument_list|,
name|type
argument_list|,
literal|false
argument_list|,
name|valueFactory
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|dispose
parameter_list|()
block|{
comment|// do nothing
block|}
block|}
end_class

end_unit

