package com.general_hello.commands.commands.RankingSystem;

import com.general_hello.commands.commands.GetData;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;

import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class LevelPointManager{

    private static final int POINTS_PER_MESSAGE = 1;
    //in seconds
    private static final int DELAY = 30;
    private static final Function<Long, Long> CALCULATE_LEVEL = ep -> (long) (1 / (float) (8) * Math.sqrt(ep));
    private static final Function<Long, Long> CALCULATE_EP = level -> (long) 64 * (long) Math.pow(level, 2);

    private static final ConcurrentHashMap<Guild, ConcurrentHashMap<Member, OffsetDateTime>> accessMap = new ConcurrentHashMap<>();

    public static long calculateLevel(Member member) throws SQLException {
        return calculateLevel(GetData.getLevelPoints(member));
    }

    public static long calculateLevel(long levelPoints){
        return CALCULATE_LEVEL.apply(levelPoints);
    }

    public static long calculateLevelMin(long level){
        return CALCULATE_EP.apply(level);
    }

    public static long calculateLevelMax(long level){
        return CALCULATE_EP.apply(level + 1);
    }

    public static LevelPointCard getLevelPointCard(Member member) throws SQLException {
        return new LevelPointCard(member);
    }

    public static void trackGuild(Guild guild){
        accessMap.put(guild, new ConcurrentHashMap<>());
    }

    public static void unTrackGuild(Guild guild){
        accessMap.remove(guild);
    }

    public static void trackMember(Member member){
        try{
            if(!accessMap.containsKey(member.getGuild())){
                return;
            }
            accessMap.get(member.getGuild()).put(member, OffsetDateTime.now());
        }
        catch(Exception ignore){

        }
    }

    public void unTrackMember(Member member){
        try{
            if(!accessMap.containsKey(member.getGuild())){
                return;
            }
            accessMap.get(member.getGuild()).remove(member);
        }
        catch(Exception ignore){

        }
    }

    public static synchronized void feed(Member member){
        try{
            Guild g = member.getGuild();

            if(!accessMap.containsKey(g)){
                return;
            }
            ConcurrentHashMap<Member, OffsetDateTime> gM = accessMap.get(g);
            if(!gM.containsKey(member)){
                trackMember(member);
            }
            OffsetDateTime last = gM.get(member);
            if(OffsetDateTime.now().isAfter(last.plusSeconds(DELAY))){
                System.out.println("Did not add xp to " + member.getEffectiveName() + " due to delay!");
                return;
            }
            gM.put(member, OffsetDateTime.now());

            Thread.sleep(100);
            GetData.setLevelPoints(member, GetData.getLevelPoints(member) + POINTS_PER_MESSAGE);
            System.out.println("Added xp to " + member.getEffectiveName() + "!");
        }
        catch(Exception ignore){
        }
    }

}
