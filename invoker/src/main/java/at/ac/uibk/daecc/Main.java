package at.ac.uibk.daecc;

import jFaaS.Gateway;
import jFaaS.invokers.HTTPGETInvoker;
import jFaaS.utils.PairResult;
import org.apache.commons.cli.*;

import java.util.Map;
import java.util.function.Supplier;

public class Main {

    private static CommandLine parseCli(String[] args) {
        Options options = new Options();

        options.addOption(new Option("k", true, "Iterations"));
        options.addOption(new Option("b_name", true, "Bucket name"));
        options.addOption(new Option("s_folder", true, "Source folder in bucket"));
        options.addOption(new Option("t_folder", true, "Target folder in bucket"));
        options.addOption(new Option("t_lang", true, "Target language"));
        options.addOption(new Option("s_region", true, "Service region"));
        options.getOptions().forEach(o -> o.setRequired(true));

        options.addOption(new Option("s_lang", true, "Source language"));

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();

        try {
            return parser.parse(options, args);
        } catch (ParseException e) {
            System.err.println(e.getMessage());
            formatter.printHelp("utility-name", options);
            System.exit(1);
        }
        return null;
    }

    public static void main(String[] args) throws Exception {

        final CommandLine cmd = parseCli(args);

        final var invoker = new Gateway("credentials.properties");
        final int k = Integer.parseInt(cmd.getOptionValue("k"));

        final String sourceLangCode = cmd.getOptionValue("s_lang");
        for (int i = 0; i < k; i++) {
            final var result = invoker.invokeFunction("arn:aws:lambda:us-east-1:250557507040:function:translateNV",
                    Map.of("bucketName", cmd.getOptionValue("b_name"),
                            "sourceFolder", cmd.getOptionValue("s_folder"),
                            "targetFolder", cmd.getOptionValue("t_folder"),
                            "sourceLanguageCode", (sourceLangCode == null || sourceLangCode.isEmpty()) ? "auto" : sourceLangCode,
                            "targetLanguageCode", cmd.getOptionValue("t_lang"),
                            "serviceRegion", cmd.getOptionValue("s_region")));
            System.out.println(result.getResult() + ", rtt:" + result.getRTT());
        }
    }
}