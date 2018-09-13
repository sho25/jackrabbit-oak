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
name|run
operator|.
name|cli
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
name|util
operator|.
name|EnumSet
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ClassToInstanceMap
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
name|collect
operator|.
name|Iterables
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
name|collect
operator|.
name|MutableClassToInstanceMap
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
name|collect
operator|.
name|Sets
import|;
end_import

begin_import
import|import
name|joptsimple
operator|.
name|OptionParser
import|;
end_import

begin_import
import|import
name|joptsimple
operator|.
name|OptionSet
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
name|whiteboard
operator|.
name|DefaultWhiteboard
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
name|whiteboard
operator|.
name|Whiteboard
import|;
end_import

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
import|import static
name|java
operator|.
name|util
operator|.
name|Arrays
operator|.
name|asList
import|;
end_import

begin_class
specifier|public
class|class
name|Options
block|{
specifier|private
specifier|final
name|Set
argument_list|<
name|OptionsBeanFactory
argument_list|>
name|beanFactories
init|=
name|Sets
operator|.
name|newHashSet
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|EnumSet
argument_list|<
name|OptionBeans
argument_list|>
name|oakRunOptions
decl_stmt|;
specifier|private
specifier|final
name|ClassToInstanceMap
argument_list|<
name|OptionsBean
argument_list|>
name|optionBeans
init|=
name|MutableClassToInstanceMap
operator|.
name|create
argument_list|()
decl_stmt|;
specifier|private
name|OptionSet
name|optionSet
decl_stmt|;
specifier|private
name|boolean
name|disableSystemExit
decl_stmt|;
specifier|private
name|String
name|commandName
decl_stmt|;
specifier|private
name|String
name|summary
decl_stmt|;
specifier|private
name|String
name|connectionString
decl_stmt|;
specifier|private
specifier|final
name|Whiteboard
name|whiteboard
init|=
operator|new
name|DefaultWhiteboard
argument_list|()
decl_stmt|;
specifier|private
name|String
name|tempDirectory
decl_stmt|;
specifier|public
name|Options
parameter_list|()
block|{
name|this
operator|.
name|oakRunOptions
operator|=
name|EnumSet
operator|.
name|allOf
argument_list|(
name|OptionBeans
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Options
parameter_list|(
name|OptionBeans
modifier|...
name|options
parameter_list|)
block|{
name|this
operator|.
name|oakRunOptions
operator|=
name|Sets
operator|.
name|newEnumSet
argument_list|(
name|asList
argument_list|(
name|options
argument_list|)
argument_list|,
name|OptionBeans
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
specifier|public
name|OptionSet
name|parseAndConfigure
parameter_list|(
name|OptionParser
name|parser
parameter_list|,
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|parseAndConfigure
argument_list|(
name|parser
argument_list|,
name|args
argument_list|,
literal|true
argument_list|)
return|;
block|}
comment|/**      * Parses the arguments and configures the OptionBeans      *      * @param parser option parser instance      * @param args command line arguments      * @param checkNonOptions if true then it checks that non options are specified i.e. some NodeStore is      *                        selected      * @return optionSet returned from OptionParser      */
specifier|public
name|OptionSet
name|parseAndConfigure
parameter_list|(
name|OptionParser
name|parser
parameter_list|,
name|String
index|[]
name|args
parameter_list|,
name|boolean
name|checkNonOptions
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|OptionsBeanFactory
name|o
range|:
name|Iterables
operator|.
name|concat
argument_list|(
name|oakRunOptions
argument_list|,
name|beanFactories
argument_list|)
control|)
block|{
name|OptionsBean
name|bean
init|=
name|o
operator|.
name|newInstance
argument_list|(
name|parser
argument_list|)
decl_stmt|;
name|optionBeans
operator|.
name|put
argument_list|(
name|bean
operator|.
name|getClass
argument_list|()
argument_list|,
name|bean
argument_list|)
expr_stmt|;
block|}
name|parser
operator|.
name|formatHelpWith
argument_list|(
operator|new
name|OakHelpFormatter
argument_list|(
name|optionBeans
operator|.
name|values
argument_list|()
argument_list|,
name|commandName
argument_list|,
name|summary
argument_list|,
name|connectionString
argument_list|)
argument_list|)
expr_stmt|;
name|optionSet
operator|=
name|parser
operator|.
name|parse
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|configure
argument_list|(
name|optionSet
argument_list|)
expr_stmt|;
name|checkForHelp
argument_list|(
name|parser
argument_list|)
expr_stmt|;
if|if
condition|(
name|checkNonOptions
condition|)
block|{
name|checkNonOptions
argument_list|()
expr_stmt|;
block|}
return|return
name|optionSet
return|;
block|}
specifier|public
name|OptionSet
name|getOptionSet
parameter_list|()
block|{
return|return
name|optionSet
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|public
parameter_list|<
name|T
extends|extends
name|OptionsBean
parameter_list|>
name|T
name|getOptionBean
parameter_list|(
name|Class
argument_list|<
name|T
argument_list|>
name|clazz
parameter_list|)
block|{
name|Object
name|o
init|=
name|optionBeans
operator|.
name|get
argument_list|(
name|clazz
argument_list|)
decl_stmt|;
name|checkNotNull
argument_list|(
name|o
argument_list|,
literal|"No [%s] found in [%s]"
argument_list|,
name|clazz
operator|.
name|getSimpleName
argument_list|()
argument_list|,
name|optionBeans
argument_list|)
expr_stmt|;
return|return
operator|(
name|T
operator|)
name|o
return|;
block|}
specifier|public
name|void
name|registerOptionsFactory
parameter_list|(
name|OptionsBeanFactory
name|factory
parameter_list|)
block|{
name|beanFactories
operator|.
name|add
argument_list|(
name|factory
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Options
name|withDisableSystemExit
parameter_list|()
block|{
name|this
operator|.
name|disableSystemExit
operator|=
literal|true
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|Options
name|setCommandName
parameter_list|(
name|String
name|commandName
parameter_list|)
block|{
name|this
operator|.
name|commandName
operator|=
name|commandName
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|Options
name|setSummary
parameter_list|(
name|String
name|summary
parameter_list|)
block|{
name|this
operator|.
name|summary
operator|=
name|summary
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|Options
name|setConnectionString
parameter_list|(
name|String
name|connectionString
parameter_list|)
block|{
name|this
operator|.
name|connectionString
operator|=
name|connectionString
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|Options
name|setTempDirectory
parameter_list|(
name|String
name|directory
parameter_list|)
block|{
name|this
operator|.
name|tempDirectory
operator|=
name|directory
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|String
name|getTempDirectory
parameter_list|()
block|{
return|return
name|tempDirectory
return|;
block|}
specifier|public
name|CommonOptions
name|getCommonOpts
parameter_list|()
block|{
return|return
name|getOptionBean
argument_list|(
name|CommonOptions
operator|.
name|class
argument_list|)
return|;
block|}
specifier|public
name|Whiteboard
name|getWhiteboard
parameter_list|()
block|{
return|return
name|whiteboard
return|;
block|}
specifier|private
name|void
name|configure
parameter_list|(
name|OptionSet
name|optionSet
parameter_list|)
block|{
for|for
control|(
name|OptionsBean
name|bean
range|:
name|optionBeans
operator|.
name|values
argument_list|()
control|)
block|{
name|bean
operator|.
name|configure
argument_list|(
name|optionSet
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|checkForHelp
parameter_list|(
name|OptionParser
name|parser
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|optionBeans
operator|.
name|containsKey
argument_list|(
name|CommonOptions
operator|.
name|class
argument_list|)
operator|&&
name|getCommonOpts
argument_list|()
operator|.
name|isHelpRequested
argument_list|()
condition|)
block|{
name|parser
operator|.
name|printHelpOn
argument_list|(
name|System
operator|.
name|out
argument_list|)
expr_stmt|;
name|systemExit
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|checkNonOptions
parameter_list|()
throws|throws
name|IOException
block|{
comment|//Some non option should be provided to enable
if|if
condition|(
name|optionBeans
operator|.
name|containsKey
argument_list|(
name|CommonOptions
operator|.
name|class
argument_list|)
operator|&&
name|getCommonOpts
argument_list|()
operator|.
name|getNonOptions
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"NodeStore details not provided"
argument_list|)
expr_stmt|;
name|systemExit
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|systemExit
parameter_list|(
name|int
name|code
parameter_list|)
block|{
if|if
condition|(
operator|!
name|disableSystemExit
condition|)
block|{
name|System
operator|.
name|exit
argument_list|(
name|code
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

