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
name|plugins
operator|.
name|document
operator|.
name|cache
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Predicate
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
name|hash
operator|.
name|BloomFilter
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
name|hash
operator|.
name|Funnel
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
name|hash
operator|.
name|PrimitiveSink
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

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Closeable
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

begin_class
specifier|public
class|class
name|CacheChangesTracker
implements|implements
name|Closeable
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|CacheChangesTracker
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|static
specifier|final
name|int
name|ENTRIES_SCOPED
init|=
literal|1000
decl_stmt|;
specifier|static
specifier|final
name|int
name|ENTRIES_OPEN
init|=
literal|10000
decl_stmt|;
specifier|private
specifier|final
name|List
argument_list|<
name|CacheChangesTracker
argument_list|>
name|changeTrackers
decl_stmt|;
specifier|private
specifier|final
name|Predicate
argument_list|<
name|String
argument_list|>
name|keyFilter
decl_stmt|;
specifier|private
specifier|final
name|LazyBloomFilter
name|lazyBloomFilter
decl_stmt|;
name|CacheChangesTracker
parameter_list|(
name|Predicate
argument_list|<
name|String
argument_list|>
name|keyFilter
parameter_list|,
name|List
argument_list|<
name|CacheChangesTracker
argument_list|>
name|changeTrackers
parameter_list|,
name|int
name|bloomFilterSize
parameter_list|)
block|{
name|this
operator|.
name|changeTrackers
operator|=
name|changeTrackers
expr_stmt|;
name|this
operator|.
name|keyFilter
operator|=
name|keyFilter
expr_stmt|;
name|this
operator|.
name|lazyBloomFilter
operator|=
operator|new
name|LazyBloomFilter
argument_list|(
name|bloomFilterSize
argument_list|)
expr_stmt|;
name|changeTrackers
operator|.
name|add
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|putDocument
parameter_list|(
name|String
name|key
parameter_list|)
block|{
if|if
condition|(
name|keyFilter
operator|.
name|apply
argument_list|(
name|key
argument_list|)
condition|)
block|{
name|lazyBloomFilter
operator|.
name|put
argument_list|(
name|key
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|invalidateDocument
parameter_list|(
name|String
name|key
parameter_list|)
block|{
if|if
condition|(
name|keyFilter
operator|.
name|apply
argument_list|(
name|key
argument_list|)
condition|)
block|{
name|lazyBloomFilter
operator|.
name|put
argument_list|(
name|key
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|boolean
name|mightBeenAffected
parameter_list|(
name|String
name|key
parameter_list|)
block|{
return|return
name|keyFilter
operator|.
name|apply
argument_list|(
name|key
argument_list|)
operator|&&
name|lazyBloomFilter
operator|.
name|mightContain
argument_list|(
name|key
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
block|{
name|changeTrackers
operator|.
name|remove
argument_list|(
name|this
argument_list|)
expr_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
if|if
condition|(
name|lazyBloomFilter
operator|.
name|filter
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Disposing CacheChangesTracker for {}, no filter was needed"
argument_list|,
name|keyFilter
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Disposing CacheChangesTracker for {}, filter fpp was: {}"
argument_list|,
name|keyFilter
argument_list|,
name|lazyBloomFilter
operator|.
name|filter
operator|.
name|expectedFpp
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|public
specifier|static
class|class
name|LazyBloomFilter
block|{
specifier|private
specifier|static
specifier|final
name|double
name|FPP
init|=
literal|0.01d
decl_stmt|;
specifier|private
specifier|final
name|int
name|entries
decl_stmt|;
specifier|private
specifier|volatile
name|BloomFilter
argument_list|<
name|String
argument_list|>
name|filter
decl_stmt|;
specifier|public
name|LazyBloomFilter
parameter_list|(
name|int
name|entries
parameter_list|)
block|{
name|this
operator|.
name|entries
operator|=
name|entries
expr_stmt|;
block|}
specifier|public
specifier|synchronized
name|void
name|put
parameter_list|(
name|String
name|entry
parameter_list|)
block|{
name|getFilter
argument_list|()
operator|.
name|put
argument_list|(
name|entry
argument_list|)
expr_stmt|;
block|}
specifier|public
name|boolean
name|mightContain
parameter_list|(
name|String
name|entry
parameter_list|)
block|{
if|if
condition|(
name|filter
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
else|else
block|{
synchronized|synchronized
init|(
name|this
init|)
block|{
return|return
name|filter
operator|.
name|mightContain
argument_list|(
name|entry
argument_list|)
return|;
block|}
block|}
block|}
specifier|private
name|BloomFilter
argument_list|<
name|String
argument_list|>
name|getFilter
parameter_list|()
block|{
if|if
condition|(
name|filter
operator|==
literal|null
condition|)
block|{
name|filter
operator|=
name|BloomFilter
operator|.
name|create
argument_list|(
operator|new
name|Funnel
argument_list|<
name|String
argument_list|>
argument_list|()
block|{
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
operator|-
literal|7114267990225941161L
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|funnel
parameter_list|(
name|String
name|from
parameter_list|,
name|PrimitiveSink
name|into
parameter_list|)
block|{
name|into
operator|.
name|putUnencodedChars
argument_list|(
name|from
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|,
name|entries
argument_list|,
name|FPP
argument_list|)
expr_stmt|;
block|}
return|return
name|filter
return|;
block|}
block|}
block|}
end_class

end_unit

