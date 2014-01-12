package atomicstryker.minions.client;

import java.io.File;
import java.io.FileInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.sound.SoundLoadEvent;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class MinionsSounds
{
    private static final String SOUND_RESOURCE_LOCATION = "assets/minions/sounds";
    private static final String SOUND_PREFIX = "minions";

    @SubscribeEvent
    public void onSoundLoad(SoundLoadEvent event)
    {
        System.out.println("SoundLoadEvent Minions, trying to load sounds");
        
        for (ModContainer mco : Loader.instance().getModList())
        {
            if (mco.getModId().equals("AS_Minions"))
            {
                System.out.println("Found Minions file base: "+mco.getSource());
                File fileCandidate = new File(mco.getSource().toURI().resolve(SOUND_RESOURCE_LOCATION));
                if (fileCandidate.isDirectory())
                {
                    System.out.println("Directory detected! Iterating...");
                    for (String soundFile : fileCandidate.list())
                    {
                        event.manager.func_148599_a(new PositionedSoundRecord(new ResourceLocation(SOUND_PREFIX, soundFile), 1.0f, 0, 0, 0, 0), 0);
                        System.out.println("loaded soundfile " + soundFile);
                    }
                }
                else if (mco.getSource().isFile() && mco.getSource().getName().endsWith(".zip"))
                {
                    System.out.println("Zip file detected! Opening...");
                    try
                    {
                        FileInputStream input = new FileInputStream(mco.getSource());
                        ZipInputStream zis = new ZipInputStream(input);
                        ZipEntry ze;
                        String s;
                        while ((ze = zis.getNextEntry()) != null)
                        {
                            if (!ze.isDirectory())
                            {
                                s = ze.getName();
                                if (s != null
                                && s.length() > 0
                                && s.startsWith(SOUND_RESOURCE_LOCATION))
                                {
                                    event.manager.func_148599_a(new PositionedSoundRecord(new ResourceLocation(SOUND_PREFIX, s.substring(s.lastIndexOf("/")+1)), 1.0f, 0, 0, 0, 0), 0);
                                    System.out.println("loaded soundfile " + s.substring(s.lastIndexOf("/")+1));
                                }
                            }
                            zis.closeEntry();
                        }
                        zis.close();
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
                break;
            }
        }
    }
}
