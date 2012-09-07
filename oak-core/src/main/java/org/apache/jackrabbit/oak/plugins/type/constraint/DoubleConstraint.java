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
name|plugins
operator|.
name|type
operator|.
name|constraint
package|;
end_package

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
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_class
specifier|public
class|class
name|DoubleConstraint
extends|extends
name|NumericConstraint
argument_list|<
name|Double
argument_list|>
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|DoubleConstraint
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
name|DoubleConstraint
parameter_list|(
name|String
name|definition
parameter_list|)
block|{
name|super
argument_list|(
name|definition
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|Double
name|getBound
parameter_list|(
name|String
name|bound
parameter_list|)
block|{
return|return
name|bound
operator|==
literal|null
operator|||
name|bound
operator|.
name|isEmpty
argument_list|()
condition|?
literal|null
else|:
name|Double
operator|.
name|parseDouble
argument_list|(
name|bound
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|protected
name|Double
name|getValue
parameter_list|(
name|Value
name|value
parameter_list|)
throws|throws
name|RepositoryException
block|{
return|return
name|value
operator|.
name|getDouble
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|protected
name|boolean
name|less
parameter_list|(
name|Double
name|val
parameter_list|,
name|Double
name|bound
parameter_list|)
block|{
return|return
name|val
operator|<
name|bound
return|;
block|}
block|}
end_class

end_unit

