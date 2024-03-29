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
name|upgrade
operator|.
name|cli
operator|.
name|parser
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
name|joptsimple
operator|.
name|OptionSet
import|;
end_import

begin_class
specifier|public
specifier|final
class|class
name|MigrationCliArguments
block|{
specifier|private
specifier|final
name|OptionSet
name|options
decl_stmt|;
specifier|private
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|arguments
decl_stmt|;
specifier|public
name|MigrationCliArguments
parameter_list|(
name|OptionSet
name|options
parameter_list|)
throws|throws
name|CliArgumentException
block|{
name|this
operator|.
name|options
operator|=
name|options
expr_stmt|;
name|this
operator|.
name|arguments
operator|=
name|getNonOptionArguments
argument_list|()
expr_stmt|;
block|}
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|getNonOptionArguments
parameter_list|()
block|{
name|List
argument_list|<
name|String
argument_list|>
name|args
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|Object
name|o
range|:
name|options
operator|.
name|nonOptionArguments
argument_list|()
control|)
block|{
name|args
operator|.
name|add
argument_list|(
name|o
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|args
return|;
block|}
specifier|public
name|boolean
name|hasOption
parameter_list|(
name|String
name|optionName
parameter_list|)
block|{
return|return
name|options
operator|.
name|has
argument_list|(
name|optionName
argument_list|)
return|;
block|}
specifier|public
name|String
name|getOption
parameter_list|(
name|String
name|optionName
parameter_list|)
block|{
return|return
operator|(
name|String
operator|)
name|options
operator|.
name|valueOf
argument_list|(
name|optionName
argument_list|)
return|;
block|}
specifier|public
name|int
name|getIntOption
parameter_list|(
name|String
name|optionName
parameter_list|)
block|{
return|return
operator|(
name|Integer
operator|)
name|options
operator|.
name|valueOf
argument_list|(
name|optionName
argument_list|)
return|;
block|}
specifier|public
name|Boolean
name|getBooleanOption
parameter_list|(
name|String
name|optionName
parameter_list|)
block|{
return|return
operator|(
name|Boolean
operator|)
name|options
operator|.
name|valueOf
argument_list|(
name|optionName
argument_list|)
return|;
block|}
specifier|public
name|String
index|[]
name|getOptionList
parameter_list|(
name|String
name|optionName
parameter_list|)
block|{
name|String
name|option
init|=
name|getOption
argument_list|(
name|optionName
argument_list|)
decl_stmt|;
if|if
condition|(
name|option
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
else|else
block|{
return|return
name|option
operator|.
name|split
argument_list|(
literal|","
argument_list|)
return|;
block|}
block|}
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getArguments
parameter_list|()
block|{
return|return
name|arguments
return|;
block|}
block|}
end_class

end_unit

