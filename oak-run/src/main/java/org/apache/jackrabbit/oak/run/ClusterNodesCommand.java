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
name|run
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PrintStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|SimpleDateFormat
import|;
end_import

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
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Comparator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Date
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
name|List
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
name|java
operator|.
name|util
operator|.
name|TimeZone
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
name|plugins
operator|.
name|document
operator|.
name|ClusterNodeInfoDocument
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
name|plugins
operator|.
name|document
operator|.
name|DocumentNodeStoreBuilder
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
name|plugins
operator|.
name|document
operator|.
name|DocumentStore
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
name|plugins
operator|.
name|document
operator|.
name|rdb
operator|.
name|RDBJSONSupport
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
name|run
operator|.
name|commons
operator|.
name|Command
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
name|io
operator|.
name|Closer
import|;
end_import

begin_import
import|import
name|joptsimple
operator|.
name|OptionSpec
import|;
end_import

begin_class
class|class
name|ClusterNodesCommand
implements|implements
name|Command
block|{
annotation|@
name|Override
specifier|public
name|void
name|execute
parameter_list|(
name|String
modifier|...
name|args
parameter_list|)
throws|throws
name|Exception
block|{
name|Closer
name|closer
init|=
name|Closer
operator|.
name|create
argument_list|()
decl_stmt|;
try|try
block|{
name|String
name|h
init|=
literal|"clusternodes mongodb://host:port/database|jdbc:..."
decl_stmt|;
name|ClusterNodesOptions
name|options
init|=
operator|new
name|ClusterNodesOptions
argument_list|(
name|h
argument_list|)
operator|.
name|parse
argument_list|(
name|args
argument_list|)
decl_stmt|;
if|if
condition|(
name|options
operator|.
name|isHelp
argument_list|()
condition|)
block|{
name|options
operator|.
name|printHelpOn
argument_list|(
name|System
operator|.
name|out
argument_list|)
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
name|DocumentNodeStoreBuilder
argument_list|<
name|?
argument_list|>
name|builder
init|=
name|Utils
operator|.
name|createDocumentMKBuilder
argument_list|(
name|options
argument_list|,
name|closer
argument_list|)
decl_stmt|;
if|if
condition|(
name|builder
operator|==
literal|null
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Clusternodes command only available for DocumentNodeStore backed by MongoDB or RDB persistence"
argument_list|)
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|setReadOnlyMode
argument_list|()
expr_stmt|;
name|DocumentStore
name|ds
init|=
name|builder
operator|.
name|getDocumentStore
argument_list|()
decl_stmt|;
try|try
block|{
name|List
argument_list|<
name|ClusterNodeInfoDocument
argument_list|>
name|all
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|ClusterNodeInfoDocument
operator|.
name|all
argument_list|(
name|ds
argument_list|)
argument_list|)
decl_stmt|;
name|Collections
operator|.
name|sort
argument_list|(
name|all
argument_list|,
operator|new
name|Comparator
argument_list|<
name|ClusterNodeInfoDocument
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
name|ClusterNodeInfoDocument
name|one
parameter_list|,
name|ClusterNodeInfoDocument
name|two
parameter_list|)
block|{
return|return
name|Integer
operator|.
name|compare
argument_list|(
name|one
operator|.
name|getClusterId
argument_list|()
argument_list|,
name|two
operator|.
name|getClusterId
argument_list|()
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
if|if
condition|(
name|options
operator|.
name|isRaw
argument_list|()
condition|)
block|{
name|printRaw
argument_list|(
name|all
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|print
argument_list|(
name|all
argument_list|,
name|options
operator|.
name|isVerbose
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|(
name|System
operator|.
name|err
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
throw|throw
name|closer
operator|.
name|rethrow
argument_list|(
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
name|closer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
name|void
name|print
parameter_list|(
name|List
argument_list|<
name|ClusterNodeInfoDocument
argument_list|>
name|docs
parameter_list|,
name|boolean
name|verbose
parameter_list|)
block|{
name|String
name|sId
init|=
literal|"Id"
decl_stmt|;
name|String
name|sState
init|=
literal|"State"
decl_stmt|;
name|String
name|sStarted
init|=
literal|"Started"
decl_stmt|;
name|String
name|sLeaseEnd
init|=
literal|"LeaseEnd"
decl_stmt|;
name|String
name|sRecoveryBy
init|=
literal|"RecoveryBy"
decl_stmt|;
name|String
name|sLeft
init|=
literal|"Left"
decl_stmt|;
name|String
name|sLastRootRev
init|=
literal|"LastRootRev"
decl_stmt|;
name|String
name|sOakVersion
init|=
literal|"OakVersion"
decl_stmt|;
name|long
name|now
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|header
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|header
operator|.
name|addAll
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|String
index|[]
block|{
name|sId
block|,
name|sState
block|,
name|sStarted
block|,
name|sLeaseEnd
block|,
name|sLeft
block|,
name|sRecoveryBy
block|}
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|verbose
condition|)
block|{
name|header
operator|.
name|add
argument_list|(
name|sLastRootRev
argument_list|)
expr_stmt|;
name|header
operator|.
name|add
argument_list|(
name|sOakVersion
argument_list|)
expr_stmt|;
block|}
name|SimpleDateFormat
name|df
init|=
operator|new
name|SimpleDateFormat
argument_list|(
literal|"yyyyMMdd'T'HHmmss'Z'"
argument_list|)
decl_stmt|;
name|df
operator|.
name|setTimeZone
argument_list|(
name|TimeZone
operator|.
name|getTimeZone
argument_list|(
literal|"UTC"
argument_list|)
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|>
name|body
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|ClusterNodeInfoDocument
name|c
range|:
name|docs
control|)
block|{
name|long
name|start
init|=
name|c
operator|.
name|getStartTime
argument_list|()
decl_stmt|;
name|long
name|leaseEnd
decl_stmt|;
name|long
name|left
decl_stmt|;
try|try
block|{
name|leaseEnd
operator|=
name|c
operator|.
name|getLeaseEndTime
argument_list|()
expr_stmt|;
name|left
operator|=
operator|(
name|leaseEnd
operator|-
name|now
operator|)
operator|/
literal|1000
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
name|leaseEnd
operator|=
literal|0
expr_stmt|;
name|left
operator|=
name|Long
operator|.
name|MIN_VALUE
expr_stmt|;
block|}
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|e
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|e
operator|.
name|put
argument_list|(
name|sId
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|c
operator|.
name|getClusterId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|e
operator|.
name|put
argument_list|(
name|sState
argument_list|,
name|c
operator|.
name|isActive
argument_list|()
condition|?
literal|"ACTIVE"
else|:
literal|"INACTIVE"
argument_list|)
expr_stmt|;
name|e
operator|.
name|put
argument_list|(
name|sStarted
argument_list|,
name|start
operator|<=
literal|0
condition|?
literal|"-"
else|:
name|df
operator|.
name|format
argument_list|(
operator|new
name|Date
argument_list|(
name|start
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|e
operator|.
name|put
argument_list|(
name|sLeaseEnd
argument_list|,
name|leaseEnd
operator|==
literal|0
condition|?
literal|"-"
else|:
name|df
operator|.
name|format
argument_list|(
operator|new
name|Date
argument_list|(
name|leaseEnd
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|e
operator|.
name|put
argument_list|(
name|sLeft
argument_list|,
operator|(
name|left
operator|<
operator|-
literal|999
operator|)
condition|?
literal|"-"
else|:
operator|(
name|Long
operator|.
name|toString
argument_list|(
name|left
argument_list|)
operator|+
literal|"s"
operator|)
argument_list|)
expr_stmt|;
name|e
operator|.
name|put
argument_list|(
name|sRecoveryBy
argument_list|,
name|c
operator|.
name|getRecoveryBy
argument_list|()
operator|==
literal|null
condition|?
operator|(
name|c
operator|.
name|isRecoveryNeeded
argument_list|(
name|now
argument_list|)
condition|?
literal|"!"
else|:
literal|"-"
operator|)
else|:
name|Long
operator|.
name|toString
argument_list|(
name|c
operator|.
name|getRecoveryBy
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|verbose
condition|)
block|{
name|e
operator|.
name|put
argument_list|(
name|sLastRootRev
argument_list|,
name|c
operator|.
name|getLastWrittenRootRev
argument_list|()
argument_list|)
expr_stmt|;
name|Object
name|oakVersion
init|=
name|c
operator|.
name|get
argument_list|(
literal|"oakVersion"
argument_list|)
decl_stmt|;
name|e
operator|.
name|put
argument_list|(
name|sOakVersion
argument_list|,
name|oakVersion
operator|==
literal|null
condition|?
literal|"-"
else|:
name|oakVersion
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|body
operator|.
name|add
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
name|list
argument_list|(
name|System
operator|.
name|out
argument_list|,
name|header
argument_list|,
name|body
argument_list|)
expr_stmt|;
block|}
comment|/**      * A generic method to print a table, choosing column widths automatically      * based both on column title and values.      *       * @param out      *            output target      * @param header      *            list of column titles      * @param body      *            list of rows, where each row is a map from column title to      *            value      */
specifier|private
specifier|static
name|void
name|list
parameter_list|(
name|PrintStream
name|out
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|header
parameter_list|,
name|List
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|>
name|body
parameter_list|)
block|{
comment|// find column widths
name|Map
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|widths
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|h
range|:
name|header
control|)
block|{
name|widths
operator|.
name|put
argument_list|(
name|h
argument_list|,
name|h
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|m
range|:
name|body
control|)
block|{
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
name|e
range|:
name|m
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|int
name|current
init|=
name|widths
operator|.
name|get
argument_list|(
name|e
operator|.
name|getKey
argument_list|()
argument_list|)
decl_stmt|;
name|int
name|thisone
init|=
name|e
operator|.
name|getValue
argument_list|()
operator|.
name|length
argument_list|()
decl_stmt|;
name|widths
operator|.
name|put
argument_list|(
name|e
operator|.
name|getKey
argument_list|()
argument_list|,
name|Math
operator|.
name|max
argument_list|(
name|current
argument_list|,
name|thisone
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|StringBuilder
name|sformat
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|h
range|:
name|header
control|)
block|{
if|if
condition|(
name|sformat
operator|.
name|length
argument_list|()
operator|!=
literal|0
condition|)
block|{
name|sformat
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
block|}
name|sformat
operator|.
name|append
argument_list|(
literal|"%"
operator|+
name|widths
operator|.
name|get
argument_list|(
name|h
argument_list|)
operator|+
literal|"s"
argument_list|)
expr_stmt|;
block|}
name|String
name|format
init|=
name|sformat
operator|.
name|toString
argument_list|()
decl_stmt|;
name|out
operator|.
name|println
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|format
argument_list|,
name|header
operator|.
name|toArray
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|m
range|:
name|body
control|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|l
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|h
range|:
name|header
control|)
block|{
name|l
operator|.
name|add
argument_list|(
name|m
operator|.
name|get
argument_list|(
name|h
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|println
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|format
argument_list|,
name|l
operator|.
name|toArray
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
name|void
name|printRaw
parameter_list|(
name|Iterable
argument_list|<
name|ClusterNodeInfoDocument
argument_list|>
name|docs
parameter_list|)
block|{
name|Map
argument_list|<
name|Object
argument_list|,
name|Object
argument_list|>
name|rawEntries
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|ClusterNodeInfoDocument
name|c
range|:
name|docs
control|)
block|{
name|Map
argument_list|<
name|Object
argument_list|,
name|Object
argument_list|>
name|entries
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|k
range|:
name|c
operator|.
name|keySet
argument_list|()
control|)
block|{
name|entries
operator|.
name|put
argument_list|(
name|k
argument_list|,
name|c
operator|.
name|get
argument_list|(
name|k
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|rawEntries
operator|.
name|put
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|c
operator|.
name|getClusterId
argument_list|()
argument_list|)
argument_list|,
name|entries
argument_list|)
expr_stmt|;
block|}
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|RDBJSONSupport
operator|.
name|appendJsonMap
argument_list|(
name|sb
argument_list|,
name|rawEntries
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|sb
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
specifier|final
class|class
name|ClusterNodesOptions
extends|extends
name|Utils
operator|.
name|NodeStoreOptions
block|{
specifier|final
name|OptionSpec
argument_list|<
name|Void
argument_list|>
name|raw
decl_stmt|;
specifier|final
name|OptionSpec
argument_list|<
name|Void
argument_list|>
name|verbose
decl_stmt|;
name|ClusterNodesOptions
parameter_list|(
name|String
name|usage
parameter_list|)
block|{
name|super
argument_list|(
name|usage
argument_list|)
expr_stmt|;
name|raw
operator|=
name|parser
operator|.
name|accepts
argument_list|(
literal|"raw"
argument_list|,
literal|"List raw entries in JSON format"
argument_list|)
expr_stmt|;
name|verbose
operator|=
name|parser
operator|.
name|accepts
argument_list|(
literal|"verbose"
argument_list|,
literal|"Be more verbose"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|ClusterNodesOptions
name|parse
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
block|{
name|super
operator|.
name|parse
argument_list|(
name|args
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
name|boolean
name|isRaw
parameter_list|()
block|{
return|return
name|options
operator|.
name|has
argument_list|(
name|raw
argument_list|)
return|;
block|}
name|boolean
name|isVerbose
parameter_list|()
block|{
return|return
name|options
operator|.
name|has
argument_list|(
name|verbose
argument_list|)
return|;
block|}
name|boolean
name|isHelp
parameter_list|()
block|{
return|return
name|options
operator|.
name|has
argument_list|(
name|help
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

